package org.dcis.services;

import org.dcis.cim.proto.CIMResponse;
import org.dcis.cim.proto.CIMServiceGrpc;
import org.dcis.cim.proto.ItineraryRequest;
import org.dcis.grpc.client.CIMChannel;
import org.json.JSONArray;
import org.json.JSONObject;

import org.dcis.cam.proto.CAMRequest;
import org.dcis.cam.proto.CAMResponse;
import org.dcis.grpc.client.CAMChannel;
import org.dcis.cam.proto.CAMServiceGrpc;

import java.util.List;

public class AcquisitionServices {
    public static void shareWithBackEnd(String key, JSONObject data) {
        CAMServiceGrpc.CAMServiceBlockingStub stub =
                CAMServiceGrpc.newBlockingStub(CAMChannel.getInstance().getChannel());
        stub.setContext(CAMRequest.newBuilder()
                .setDataType(CAMRequest.TYPE.ANIMAL)
                .setIdentifier(key).setData(data.toString())
                .build());
    }

    public static JSONObject verifyBLE(String key) {
        CAMServiceGrpc.CAMServiceBlockingStub stub =
                CAMServiceGrpc.newBlockingStub(CAMChannel.getInstance().getChannel());
        CAMResponse response = stub.verifyProvider(CAMRequest.newBuilder()
                .setIdentifier(key)
                .build());

        JSONObject verification = new JSONObject();
        if(response.getStatus() == 200)
            verification.put("session", response.getBody());
        else {
            switch (response.getStatus()){
                case 400 -> verification.put("error", "Unauthorised bluetooth sharing.");
                case 404 -> verification.put("error", "BLE not found.");
                case 500 -> verification.put("error", "Error in the verification process");
            }
        }
        return verification;
    }

    public static JSONObject nearingEnclosure(String tag) {
        CAMServiceGrpc.CAMServiceBlockingStub stub =
                CAMServiceGrpc.newBlockingStub(CAMChannel.getInstance().getChannel());
        CAMResponse response = stub.getDescription(CAMRequest.newBuilder()
                .setIdentifier(tag)
                .build());
        JSONObject body = new JSONObject();
        if(response.getStatus() == 200) {
            body.put("message", response.getBody());
            return body;
        }
        body.put("error", "Couldn't prefetch the metadata.");
        return body;
    }

    public static JSONArray getInitialItinerary(List<String> preferences) {
        CIMServiceGrpc.CIMServiceBlockingStub stub =
                CIMServiceGrpc.newBlockingStub(CIMChannel.getInstance().getChannel());
        CIMResponse response = stub.getInitialItinerary(
                ItineraryRequest.newBuilder()
                        .addAllPreferences(preferences)
                        .build());
        if(response.getStatus() == 200)
            return new JSONArray(response.getBody());

        return new JSONArray();
    }
}
