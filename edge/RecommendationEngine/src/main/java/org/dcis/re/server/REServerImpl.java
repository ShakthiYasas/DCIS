package org.dcis.re.server;

import io.grpc.stub.StreamObserver;

import org.dcis.re.proto.*;
import org.dcis.re.services.OptimalRouteService;
import org.dcis.re.services.RecommendationService;

public class REServerImpl extends REServiceGrpc.REServiceImplBase{

    // Flags an enclosure as visited.
    // Response: None. Status only.
    public void setVisited(RERequest request,
                           StreamObserver<REResponse> responseObserver){
        try {
            OptimalRouteService.setVisited(request.getVisited());
            responseObserver.onNext(REResponse.newBuilder().setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Calculates and shares the initial and subsequent alternate itineraries.
    // Response: String converted JSON Array of enclosures and intersections.
    public void getItinerary(ItineraryRequest request,
                             StreamObserver<REResponse> responseObserver){
        try {
            responseObserver.onNext(REResponse.newBuilder()
                            .setBody(OptimalRouteService
                                    .getItinerary(request.getFrom(),
                                            request.getPreferredMap(),
                                            request.getOnway()))
                            .setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Calculates the shortest path between 2 enclosures.
    // Response: String converted JSON Array of enclosures and intersections.
    public void getRoute(RouteRequest request,
                         StreamObserver<REResponse> responseObserver){
        try {
            responseObserver.onNext(REResponse.newBuilder()
                            .setBody(OptimalRouteService
                                    .getRoute(request.getFrom(),request.getTo()))
                            .setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Retrieve recommendations of alternate enclosures based on the last most enthusiastically visited enclosure.
    // Response: Prioritized list of enclosure tags.
    public void getAlternates(RERequest request,
                         StreamObserver<REResponse> responseObserver){
        try {
            RecommendationService recser = new RecommendationService();
            responseObserver.onNext(REResponse.newBuilder()
                    .setBody(recser.recommendForVisitor(
                            request.getVisited(), request.getCount()))
                    .setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
