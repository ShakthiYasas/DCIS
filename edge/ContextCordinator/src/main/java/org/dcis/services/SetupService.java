package org.dcis.services;

import org.dcis.cam.proto.CAMRequest;
import org.dcis.cam.proto.CAMResponse;
import org.dcis.cim.proto.CIMResponse;
import org.dcis.cim.proto.CIMServiceGrpc;
import org.dcis.cim.proto.SiddhiRequest;
import org.dcis.grpc.client.CAMChannel;
import org.dcis.cam.proto.CAMServiceGrpc;

import org.dcis.grpc.client.CIMChannel;
import org.json.JSONObject;

public class SetupService {
    public static JSONObject setupEdge(JSONObject config) {
        boolean status = true;

        // 1. Retrieves the exhaustSituation function and caches it.
        // 2. Sets the audience in GenAI Invoker.
        CAMServiceGrpc.CAMServiceBlockingStub cam_stub =
                CAMServiceGrpc.newBlockingStub(CAMChannel.getInstance().getChannel());
        CAMResponse res_cam = cam_stub.retrieveSituations(CAMRequest.newBuilder()
                .setData(config.getString("audience"))
                .setIdentifier("exhaustSituation").build());
        status &= (res_cam.getStatus() == 200);

        // 3. Start a SiddhiApp.
        CIMServiceGrpc.CIMServiceBlockingStub cim_stub =
                CIMServiceGrpc.newBlockingStub(CIMChannel.getInstance().getChannel());
        CIMResponse res_cim = cim_stub.startSiddhi(SiddhiRequest.newBuilder()
                .setName(config.getString("session-id"))
                .build());
        status &= (res_cim.getStatus() == 200);

        // 4. Executes setQuery if the health parameters are retrieved.
        if(config.getBoolean("health-permission")) {
            CIMResponse res_cim_2 = cim_stub.setQuery(SiddhiRequest.newBuilder()
                    .setName(config.getString("abnormalheart"))
                    .build());
            status &= (res_cim_2.getStatus() == 200);

            CIMResponse res_cim_3 = cim_stub.setQuery(SiddhiRequest.newBuilder()
                    .setName(config.getString("exhausted"))
                    .build());
            status &= (res_cim_3.getStatus() == 200);
        }

        JSONObject response = new JSONObject();

        if(status) {
            response.put("status", 200);
            return response;
        }

        response.put("status", 500);
        response.put("message", "One or more errored during setup");
        return response;
    }

    public static void deactivate() {
        CIMServiceGrpc.CIMServiceBlockingStub cim_stub =
                CIMServiceGrpc.newBlockingStub(CIMChannel.getInstance().getChannel());
        cim_stub.stopSiddhi(SiddhiRequest.newBuilder()
                .build());
    }
}
