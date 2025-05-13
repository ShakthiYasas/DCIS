package org.dcis.cim.server;

import org.dcis.cim.handler.RecommenderWrapper;
import org.dcis.cim.proto.*;
import io.grpc.stub.StreamObserver;
import org.dcis.cim.handler.SiddhiWrapper;
import org.dcis.cim.handler.ContextReasoner;

public class CIMServerImpl extends CIMServiceGrpc.CIMServiceImplBase {

    // Calculates the probability of a situation given a situation model and context information.
    // Response: Probability of the situation.
    public void infer(CIMRequest request,
                            StreamObserver<CIMResponse> responseObserver){
        try {
            double prob = ContextReasoner.infer(request.getDescription(), request.getData());
            responseObserver.onNext(CIMResponse.newBuilder()
                            .setProb(prob).setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Creates and starts a Siddhi instance.
    // Response: None. Status only.
    public void startSiddhi(SiddhiRequest request,
                                 StreamObserver<CIMResponse> responseObserver){
        try {
            responseObserver.onNext(SiddhiWrapper.getInstance()
                    .createSiddhiApp(request.getName()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Stops the existing Siddhi instance.
    // Response: None. Status only.
    public void stopSiddhi(SiddhiRequest request,
                            StreamObserver<CIMResponse> responseObserver){
        try {
            SiddhiWrapper.getInstance().shutDownSiddhiApp();
            responseObserver.onNext(CIMResponse.newBuilder().setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Adds an event (data) in to the Siddhi stream.
    // Response: None. Status only.
    public void addEvent(SiddhiRequest request,
                         StreamObserver<CIMResponse> responseObserver){
        try {
            SiddhiWrapper.getInstance().addEvent(request.getDomain(), request.getJson());
            responseObserver.onNext(CIMResponse.newBuilder().setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Sets a new Siddhi query and its associated callback.
    // Response: None. Status only.
    public void setQuery(SiddhiRequest request,
                         StreamObserver<CIMResponse> responseObserver){
        try {
            SiddhiWrapper.getInstance().setQuery(request.getDomain(), request.getName());
            responseObserver.onNext(CIMResponse.newBuilder().setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Creates the initial itinerary for the visitor based on context and the preferences.
    // Response: The order list of destinations and their geolocations.
    public void getInitialItinerary(ItineraryRequest request,
                         StreamObserver<CIMResponse> responseObserver){
        try {
            responseObserver.onNext(CIMResponse.newBuilder()
                            .setBody(RecommenderWrapper.getInstance()
                                    .getStartingItenerary(request.getPreferencesList()))
                            .setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
