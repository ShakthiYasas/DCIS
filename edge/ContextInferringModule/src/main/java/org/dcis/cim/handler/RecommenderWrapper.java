package org.dcis.cim.handler;

import org.dcis.cam.proto.CAMRequest;
import org.dcis.cam.proto.CAMResponse;
import org.dcis.cam.proto.CAMServiceGrpc;
import org.dcis.grpc.client.CAMChannel;

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
    private final Map<String, Double> avgDuration;

    private RecommenderWrapper() {
        avgDuration = new HashMap<>();
        setDurations();
    }

    public static synchronized RecommenderWrapper getInstance() {
        if(instance == null) {
            instance = new RecommenderWrapper();
        }
        return instance;
    }

    // Flags an enclosure as visited.
    // tag: Identification tag of the enclosure.
    // returns: None.
    public void setVisitedNode(String tag) {
        REServiceGrpc.REServiceBlockingStub stub =
                REServiceGrpc.newBlockingStub(REChannel.getInstance().getChannel());
        stub.setVisited(RERequest.newBuilder()
                        .setVisited(tag).build());
    }

    // Creates the initial itinerary for a visitor.
    // preferred: The list of enclosure tags that the visitor would like to visit.
    // returns: String converted JSON Array of nodes to visit (including intersections).
    public String getStartingItenerary(List<String> preferred) {
        return retrieveItinerary("entexit",
                getSortedPrefernances(preferred), false);
    }

    // Creates the initial and post visit alternate itineraries for a visitor.
    // tag: The identification tag of the node that the visitor is currently at.
    // preferred: The list of enclosure tags that the visitor would like to visit.
    // oneWay: Whether or not the visitor need to return to the current node. Always True for post visit alternate itineraries.
    // returns: String converted JSON Array of nodes to visit (including intersections).
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

    // Recommends an alternative enclosure to visit based on similarity of preference to the last visited enclosure.
    // tag: The identifier tag of the last visited enclosure.
    // waitTime: The wait time in seconds at the last visited enclosure.
    // returns: String converted JSON Array containing the top recommended enclosures to visit next.
    public String recommendAlternative(String tag, double waitTime) {
        double range = avgDuration.get(tag);
        if(waitTime >= range) {
            // Provide a recommendation because the visitor has stayed longer than usual.
            REServiceGrpc.REServiceBlockingStub stub =
                    REServiceGrpc.newBlockingStub(REChannel.getInstance().getChannel());
            REResponse response = stub.getAlternates(RERequest.newBuilder()
                    .setVisited(tag).setCount(3)
                    .build());

            if(response.getStatus() == 200)
                return response.getBody();
        }
        return null;
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

    // The values here are the max value for the 95% confidence interval.
    private void setDurations() {
        avgDuration.put("bird_enc", 254.7);
        avgDuration.put("lion_enc", 69.15);
        avgDuration.put("koala_enc", 144.3);
        avgDuration.put("penguin_enc", 202.8);
        avgDuration.put("meerkat_enc", 221.4);
        avgDuration.put("elephant_enc", 509.4);
        avgDuration.put("tortoise_enc", 69.17);
        avgDuration.put("orangutan_enc", 382.05);
    }
}
