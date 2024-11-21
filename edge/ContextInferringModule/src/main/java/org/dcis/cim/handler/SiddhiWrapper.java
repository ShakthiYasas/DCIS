package org.dcis.cim.handler;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONObject;

import io.siddhi.core.event.Event;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;

import org.dcis.cim.proto.CIMResponse;
import org.dcis.cim.proto.SiddhiRequest.DOMAIN;

public final class SiddhiWrapper {
    private String appName;
    private static SiddhiWrapper instance;
    private static SiddhiManager siddhiManager;

    private final Map<String,StreamCallback> callbacks;
    private SiddhiWrapper() {
        callbacks = new HashMap<>();
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
                    "@app:name(\"" + name + "\")" +
                    "define stream LocStream (latitude double, longitude double);" +
                    "define stream BioStream (temperature double, heart_rate double);";
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
                        event.getDouble("latitude"),
                        event.getDouble("longitude"),
                        event.getLong("timestamp"),
                        event.getString("animal")
                });
            }
            case DOMAIN.HEALTH -> {
                inputHandler = siddhiApp.getInputHandler("BioStream");
                inputHandler.send(new Object[]{
                        event.getDouble("temperature"),
                        event.getDouble("heart_rate"),
                        event.getLong("timestamp")
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

            }
            case DOMAIN.HEALTH -> {
                // Visitor may be exhausted event.
                siddhiApp.query("from BioStream#window.timeBatch(10 min, 0) " +
                        "select avg(heart_rate) as avgHeartRate, max(heart_rate) as maxHeartRate " +
                        "having avgHeartRate > 120.0 insert into " +
                        event.getString("callback_name") + ";");
                StreamCallback callback_ref = new StreamCallback() {
                    @Override
                    public void receive(Event[] events) {
                        // TODO: Invoke relevant method.
                    }
                };
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
