package org.dcis.cim.handler;

import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;
import org.dcis.cim.services.CallBackService;

import io.siddhi.core.event.Event;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;

import org.dcis.cim.proto.CIMResponse;
import org.dcis.grpc.client.CCMChannel;
import org.dcis.ccm.proto.CCMServiceGrpc;
import org.dcis.cim.proto.SiddhiRequest.DOMAIN;

public final class SiddhiWrapper {

    private String appName;
    private static SiddhiWrapper instance;
    private final ExecutorService executor;
    private static SiddhiManager siddhiManager;

    private long lastENotification;
    private long lastAHRNotification;

    private final Map<String,StreamCallback> callbacks;
    private SiddhiWrapper() {
        lastENotification = 0;
        lastAHRNotification = 0;
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
                    "define stream BioStream (body_temperature double, heart_rate double); \n" +
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
                        event.getDouble("body_temperature"),
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
    public void setQuery(DOMAIN domain, String callback_name) {
        SiddhiAppRuntime siddhiApp = siddhiManager.getSiddhiAppRuntime(this.appName);

        switch (domain) {
            case DOMAIN.LOCATION -> {
                // Stationary time retrieval.
                siddhiApp.query("from every e1=LocStream[tag == \"" + callback_name + "\" and distance > 5] " +
                        "-> e2=LocStream[tag == \"" + callback_name + "\" and distance <=5] " +
                        "-> e3=LocStream[timestamp > e2.timestamp and  " +
                        "distance >= 5 and tag == \"" + callback_name + "\"]\n" +
                                "select min(e3[0].timestamp - e1[last].timestamp) as duration, e3.tag as tag\n" +
                                "order by e1.timestamp\n" +
                                "insert into \"" + callback_name + "\"_leave;");

                StreamCallback callback_ref = new StreamCallback() {
                    @Override
                    public void receive(Event[] events) {
                        long stationary_time = (long) events[events.length-1].getData(0);
                        String tag = (String) events[events.length-1].getData(1);
                        // Removing the event monitor for the
                        removeCallback(tag);

                        // TODO: Optimise the recommendation model.

                        // Notify about the next best enclosure to visit as of now.
                        CallBackService cbService = new CallBackService();
                        cbService.sendNotification("Next Enclosure");

                        // Evicting the animal context.
                        CCMServiceGrpc.CCMServiceBlockingStub stub =
                                CCMServiceGrpc.newBlockingStub(CCMChannel.getInstance().getChannel());
                        stub.deleteInCache(CCMRequest.newBuilder()
                                .setOperation(CCMRequest.OPERATION.EVICT)
                                .setIdentifier("animal")
                                .build());
                    }
                };
                siddhiApp.addCallback(callback_name + "_leave", callback_ref);
                callbacks.put(callback_name, callback_ref);
            }
            case DOMAIN.HEALTH -> {
                StreamCallback callback_ref = null;

                if(callback_name.equals("abnormalheart")) {
                    // Abnormal heart rate warning.
                    siddhiApp.query("from BioStream#window.timeBatch(10 min, 0) \n" +
                            "select avg(heart_rate) as avgHeartRate, max(heart_rate) as maxHeartRate \n" +
                            "having avgHeartRate > 120.0 \n" +
                            "insert into " + callback_name + ";");

                    callback_ref = new StreamCallback() {
                        @Override
                        public void receive(Event[] events) {
                            // Send a notification about the condition given
                            // a notification has not been sent for the last 10 minutes.
                            long currenttime = System.currentTimeMillis();
                            if(lastAHRNotification == 0 || (currenttime - lastAHRNotification) > 600000){
                                lastAHRNotification = currenttime;
                                CallBackService cbService = new CallBackService();
                                cbService.sendWarning("health","abnormalheart");
                            }
                        }
                    };

                }
                else if(callback_name.equals("exhausted")) {
                    // Exhaustion warning based on probability.
                    siddhiApp.query("from every e1=ContextStream, " +
                            "e2=ContextStream[e1.exhaustProb < exhaustProb and (timestamp - e1.timestamp) < 300000], " +
                            "e3=ContextStream[(timestamp - e1.timestamp) > 300000 " +
                            "and e1.exhaustProb < exhaustProb and exhaustProb > 0.8] \n" +
                            "select e1.exhaustProb as startProb, e3.exhaustProb as finProb, e3.timestamp \n" +
                            "insert into " + callback_name + ";");
                    callback_ref = new StreamCallback() {
                        @Override
                        public void receive(Event[] events) {
                            // Send a notification about the condition given
                            // a notification has not been sent for the last 10 minutes.
                            long currenttime = System.currentTimeMillis();
                            if(lastENotification == 0 || (currenttime - lastENotification) > 600000) {
                                lastENotification = currenttime;
                                CallBackService cbService = new CallBackService();
                                cbService.sendWarning("health", "exhausted");
                            }
                        }
                    };
                }
                siddhiApp.addCallback(callback_name, callback_ref);
                callbacks.put(callback_name, callback_ref);
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
