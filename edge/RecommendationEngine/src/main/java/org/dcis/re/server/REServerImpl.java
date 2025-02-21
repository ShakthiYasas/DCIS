package org.dcis.re.server;

import io.grpc.stub.StreamObserver;

import org.dcis.re.proto.*;
import org.dcis.re.services.OptimalRouteService;

public class REServerImpl extends REServiceGrpc.REServiceImplBase{
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
}
