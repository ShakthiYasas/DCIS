package org.dcis.services;

import org.json.JSONObject;

import org.dcis.grpc.client.CIMChannel;
import org.dcis.cim.proto.SiddhiRequest;
import org.dcis.cim.proto.CIMServiceGrpc;

public class EventServices {
    public static void addSiddhiEvent (String domain, JSONObject data) {
        SiddhiRequest request = null;
        CIMServiceGrpc.CIMServiceFutureStub stub =
                CIMServiceGrpc.newFutureStub(CIMChannel.getInstance().getChannel());
        switch(domain) {
            case "location" -> {
                request = SiddhiRequest.newBuilder()
                        .setDomain(SiddhiRequest.DOMAIN.LOCATION)
                        .setJson(data.toString())
                        .build();
            }
            case "health" -> {
                request = SiddhiRequest.newBuilder()
                        .setDomain(SiddhiRequest.DOMAIN.HEALTH)
                        .setJson(data.toString())
                        .build();
            }
        }
        stub.addEvent(request);
    }
}
