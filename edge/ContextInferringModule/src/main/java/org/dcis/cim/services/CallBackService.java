package org.dcis.cim.services;

import org.json.JSONObject;
import org.dcis.csm.proto.CSMResponse;

import org.dcis.grpc.client.CSMChannel;

import org.dcis.csm.proto.CSMRequest;
import org.dcis.csm.proto.CSMServiceGrpc;

public class CallBackService {
    public void sendNotification(String message) {
        CSMServiceGrpc.CSMServiceBlockingStub stub =
                CSMServiceGrpc.newBlockingStub(CSMChannel.getInstance().getChannel());
        CSMResponse res = stub.sendToApp(CSMRequest.newBuilder()
                .setData(message).build());
    }

    public void sendWarning(String context, String subContext) {
        JSONObject message = new JSONObject();
        message.put("type", "warning");
        message.put("context", context);

        switch(subContext) {
            case "abnormalheart" ->
                message.put("message","Your heart rate was over 120BPM for the last 10 minutes. Are you Okay?");
            case "exhausted" ->
                message.put("message","You seems exhausted! Better take a rest.");
        }

        CSMServiceGrpc.CSMServiceBlockingStub stub =
                CSMServiceGrpc.newBlockingStub(CSMChannel.getInstance().getChannel());
        CSMResponse res = stub.sendToApp(CSMRequest.newBuilder()
                .setData(message.toString()).build());
    }
}

