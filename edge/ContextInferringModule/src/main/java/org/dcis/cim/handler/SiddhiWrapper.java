package org.dcis.cim.handler;

import io.siddhi.core.SiddhiAppRuntime;
import org.dcis.cim.proto.CIMResponse;
import org.dcis.cim.proto.SiddhiRequest;

import io.siddhi.core.SiddhiManager;

public final class SiddhiWrapper {
    private static SiddhiWrapper instance;
    private static SiddhiManager siddhiManager;

    private SiddhiWrapper() {}

    public static synchronized SiddhiWrapper getInstance() {
        if(instance == null) {
            instance = new SiddhiWrapper();
            siddhiManager = new SiddhiManager();
        }
        return instance;
    }

    public CIMResponse createSiddhiApp(SiddhiRequest request) {
        try{
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                    .createSiddhiAppRuntime(request.getJson());
            siddhiAppRuntime.start();
            return CIMResponse.newBuilder().setStatus(200).build();
        }
        catch(Exception ex) {
            return CIMResponse.newBuilder()
                    .setBody(ex.getMessage())
                    .setStatus(500).build();
        }
    }
}
