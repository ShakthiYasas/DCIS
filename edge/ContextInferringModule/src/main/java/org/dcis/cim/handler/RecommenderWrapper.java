package org.dcis.cim.handler;

import org.dcis.cam.proto.CAMRequest;
import org.dcis.cam.proto.CAMResponse;
import org.dcis.cam.proto.CAMServiceGrpc;
import org.dcis.csm.proto.CSMRequest;
import org.dcis.csm.proto.CSMServiceGrpc;
import org.dcis.grpc.client.CAMChannel;
import org.dcis.grpc.client.CSMChannel;
import org.dcis.re.proto.RERequest;
import org.dcis.re.proto.REResponse;
import org.dcis.grpc.client.REChannel;
import org.dcis.re.proto.REServiceGrpc;
import org.dcis.re.proto.ItineraryRequest;

import java.util.*;
import org.javatuples.Pair;
import org.json.JSONObject;

public class RecommenderWrapper {

    private String currentItinerary;
    private static RecommenderWrapper instance;
    private Map<String, Integer> currentPreferance;

    private RecommenderWrapper() {}

    public static synchronized RecommenderWrapper getInstance() {
        if(instance == null) {
            instance = new RecommenderWrapper();
        }
        return instance;
    }

    public void setVisitedNode(String tag) {
        REServiceGrpc.REServiceBlockingStub stub =
                REServiceGrpc.newBlockingStub(REChannel.getInstance().getChannel());
        stub.setVisited(RERequest.newBuilder()
                        .setVisited(tag).build());
    }

    public String getStartingItenerary(List<String> preferred) {
        return retrieveItinerary("entexit",
                getSortedPrefernances(preferred), false);
    }

    public String retrieveItinerary(String tag, Map<String, Integer> preferred, Boolean oneWay) {
        if(preferred == null){
            currentPreferance.remove(tag);
            currentPreferance = getSortedPrefernances(
                    currentPreferance.keySet().stream().toList());
        }
        else currentPreferance = preferred;

        REServiceGrpc.REServiceBlockingStub stub =
                REServiceGrpc.newBlockingStub(REChannel.getInstance().getChannel());
        REResponse response = stub.getItinerary(ItineraryRequest.newBuilder()
                        .setFrom(tag).setOnway(oneWay)
                        .putAllPreferred(this.currentPreferance)
                        .build());
        if(response.getStatus() == 200)
            this.currentItinerary = response.getBody();
        return currentItinerary;
    }

    private Map<String, Integer> getSortedPrefernances(List<String> preferred) {
        PriorityQueue<Pair<String,Double>> pq = new PriorityQueue<>(
                Comparator.comparingDouble(Pair::getValue1));

        for(String enc_tag : preferred)
            pq.add(new Pair<>(enc_tag, 1 - retrieveContext(enc_tag)));

        int priority = 1;
        Map<String, Integer> sortedPreference = new HashMap<>();
        while(!pq.isEmpty()){
            sortedPreference.put(pq.poll().getValue0(),priority);
            priority++;
        }

        return sortedPreference;
    }

    private Double retrieveContext(String tag) {
        CAMServiceGrpc.CAMServiceBlockingStub stub =
                CAMServiceGrpc.newBlockingStub(CAMChannel.getInstance().getChannel());
        CAMResponse response = stub.getBackUpContext(CAMRequest.newBuilder()
                        .setDataType(CAMRequest.TYPE.ANIMAL)
                        .setIdentifier(tag).build());

        if(response.getStatus() == 200){
            JSONObject context = new JSONObject(response.getBody());
            return context.getDouble("context");
        }
        return 0.0;
    }
}
