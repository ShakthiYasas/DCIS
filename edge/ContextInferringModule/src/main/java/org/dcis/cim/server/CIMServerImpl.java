package org.dcis.cim.server;

import org.dcis.cim.proto.*;
import io.grpc.stub.StreamObserver;
import org.dcis.cim.handler.SiddhiWrapper;
import org.dcis.cim.handler.ContextReasoner;

public class CIMServerImpl extends CIMServiceGrpc.CIMServiceImplBase {

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
}
