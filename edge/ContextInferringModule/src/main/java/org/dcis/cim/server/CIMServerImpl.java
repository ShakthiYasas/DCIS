package org.dcis.cim.server;

import io.grpc.stub.StreamObserver;
import org.dcis.cim.proto.CIMResponse;
import org.dcis.cim.proto.SiddhiRequest;
import org.dcis.cim.proto.CIMServiceGrpc;
import org.dcis.cim.handler.SiddhiWrapper;

public class CIMServerImpl extends CIMServiceGrpc.CIMServiceImplBase {

    public void registerInSiddhi(SiddhiRequest request,
                                 StreamObserver<CIMResponse> responseObserver){
        try {
            responseObserver.onNext(SiddhiWrapper.getInstance()
                    .createSiddhiApp(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
