package org.dcis.cim.handler;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.siddhi.core.event.Event;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;

import org.dcis.cim.proto.CIMResponse;
import org.dcis.grpc.client.CCMChannel;
import org.dcis.ccm.proto.CCMServiceGrpc;
import org.dcis.cim.proto.SiddhiRequest.DOMAIN;
import org.dcis.cim.proto.SituationDescription;

public final class SiddhiWrapper {

    private String appName;
    private static SiddhiWrapper instance;
    private final ExecutorService executor;
    private static SiddhiManager siddhiManager;

    private final Map<String,StreamCallback> callbacks;
    private SiddhiWrapper() {
        callbacks = new HashMap<>();
        executor = Executors.newSingleThreadExecutor();
    }

    public static synchronized SiddhiWrapper getInstance() {
        if(instance == null) {
            instance = new SiddhiWrapper();
            siddhiManager = new SiddhiManager();
        }
        return instance;
    }

    // name: Name of the SiddhiApp.
    public CIMResponse createSiddhiApp(String name) {
        try{
            this.appName = name;
            String appString =
                    "@app:name(\"" + name + "\") \n" +
                    "define stream LocStream (tag string, timestamp long, distance double, latitude double, longitude double); \n" +
                    "define stream BioStream (temperature double, heart_rate double); \n" +
                    "define stream ContextStream (exhaustProb double);";
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                    .createSiddhiAppRuntime(appString);
            siddhiAppRuntime.start();
            return CIMResponse.newBuilder().setStatus(200).build();
        }
        catch(Exception ex) {
            return CIMResponse.newBuilder()
                    .setBody(ex.getMessage())
                    .setStatus(500).build();
        }
    }

    // data: The event data for the input stream.
    // domain: The aspect of the visitor being monitored.
    public void addEvent(DOMAIN domain, String data) throws InterruptedException {
        InputHandler inputHandler;
        JSONObject event = new JSONObject(data);
        SiddhiAppRuntime siddhiApp = siddhiManager.getSiddhiAppRuntime(this.appName);
        switch(domain){
            case DOMAIN.LOCATION -> {
                inputHandler = siddhiApp.getInputHandler("LocStream");
                inputHandler.send(new Object[]{
                        event.getString("tag"),
                        event.getLong("timestamp"),
                        event.getDouble("distance"),
                        event.getDouble("latitude"),
                        event.getDouble("longitude"),
                });
            }
            case DOMAIN.HEALTH -> {
                inputHandler = siddhiApp.getInputHandler("BioStream");
                inputHandler.send(new Object[]{
                        event.getDouble("temperature"),
                        event.getDouble("heart_rate"),
                        event.getLong("timestamp")
                });

                executor.execute(() -> {
                    try {
                        CCMServiceGrpc.CCMServiceBlockingStub stub =
                                CCMServiceGrpc.newBlockingStub(CCMChannel.getInstance().getChannel());
                        CCMResponse response = stub.lookupCache(CCMRequest.newBuilder()
                                        .setOperation(CCMRequest.OPERATION.READ)
                                        .setIdentifier("exhaustSituation").build());

                        double prob = ContextReasoner.infer(response.getSituation(),
                                new ObjectMapper().readValue(data, HashMap.class));
                        InputHandler contextHandler =
                                siddhiApp.getInputHandler("ContextStream");
                        contextHandler.send(new Object[]{prob, event.getLong("timestamp")});
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    // data: Parameter values for the query.
    // domain: The aspect of the visitor being monitored.
    public void setQuery(DOMAIN domain, String data) {
        JSONObject event = new JSONObject(data);
        SiddhiAppRuntime siddhiApp = siddhiManager.getSiddhiAppRuntime(this.appName);
        switch (domain) {
            case DOMAIN.LOCATION -> {
                String topic = event.getString("tag");

                // Stationary time retrieval.
                siddhiApp.query("from every e1=LocStream[tag == \"" + topic + "\" and distance > 5] " +
                        "-> e2=LocStream[tag == \"" + topic + "\" and distance <=5] " +
                        "-> e3=LocStream[timestamp > e2.timestamp and  " +
                        "distance >= 5 and tag == \"" + topic + "\"]\n" +
                                "select min(e3[0].timestamp - e1[last].timestamp) as duration, e3.tag as tag\n" +
                                "order by e1.timestamp\n" +
                                "insert into \"" + topic + "\"_leave;");

                StreamCallback callback_ref = new StreamCallback() {
                    @Override
                    public void receive(Event[] events) {
                        long stationary_time = (long) events[events.length-1].getData(0);
                        String tag = (String) events[events.length-1].getData(1);
                        // TODO: Optimise the recommendation model, evict animal context, notify next enclosure.
                    }
                };
                siddhiApp.addCallback(topic + "_leave", callback_ref);
                callbacks.put(topic, callback_ref);
            }
            case DOMAIN.HEALTH -> {
                StreamCallback callback_ref = null;
                String topic = event.getString("callback_name").toLowerCase();

                if(topic.equals("abnormalheart")) {
                    // Abnormal heart rate warning.
                    siddhiApp.query("from BioStream#window.timeBatch(10 min, 0) \n" +
                            "select avg(heart_rate) as avgHeartRate, max(heart_rate) as maxHeartRate \n" +
                            "having avgHeartRate > 120.0 \n" +
                            "insert into " + event.getString("callback_name") + ";");

                    callback_ref = new StreamCallback() {
                        @Override
                        public void receive(Event[] events) {
                            // TODO: Invoke relevant method.
                        }
                    };

                }
                else if(topic.equals("exhausted")) {
                    // Exhaustion warning based on probability.
                    siddhiApp.query("from every e1=ContextStream, " +
                            "e2=ContextStream[e1.exhaustProb < exhaustProb and (timestamp - e1.timestamp) < 300000], " +
                            "e3=ContextStream[(timestamp - e1.timestamp) > 300000 " +
                            "and e1.exhaustProb < exhaustProb and exhaustProb > 0.8] \n" +
                            "select e1.exhaustProb as startProb, e3.exhaustProb as finProb, e3.timestamp \n" +
                            "insert into " + event.getString("callback_name") + ";");
                    callback_ref = new StreamCallback() {
                        @Override
                        public void receive(Event[] events) {
                            // TODO: Invoke relevant method.
                        }
                    };
                }
                siddhiApp.addCallback(event.getString("callback_name"), callback_ref);
                callbacks.put(event.getString("callback_name"), callback_ref);
            }
        }
    }

    // name: Name of the Siddhi output stream.
    public void removeCallback(String name) {
        siddhiManager.getSiddhiAppRuntime(this.appName)
                .removeCallback(callbacks.get(name));
        callbacks.remove(name);
    }

    public void shutDownSiddhiApp() {
        siddhiManager.getSiddhiAppRuntime(this.appName)
                .shutdown();
    }
}
