package org.dcis.cim.server;

import org.dcis.cim.proto.*;
import io.grpc.stub.StreamObserver;
import org.dcis.cim.handler.SiddhiWrapper;

public class CIMServerImpl extends CIMServiceGrpc.CIMServiceImplBase {

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
}
