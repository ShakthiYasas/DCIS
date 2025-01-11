package org.dcis.csm.server;

import io.grpc.stub.StreamObserver;
import org.dcis.csm.handler.CloudHandler;
import org.dcis.csm.handler.PubNubWrapper;
import org.dcis.csm.proto.CSMRequest;
import org.dcis.csm.proto.CSMResponse;
import org.dcis.csm.proto.CSMServiceGrpc;

public class CSMServerImpl extends CSMServiceGrpc.CSMServiceImplBase{
    public void sendToApp(CSMRequest request,
                          StreamObserver<CSMResponse> responseObserver){
        try {
            PubNubWrapper.getInstance().sendToApp(request.getData());
            responseObserver.onNext(CSMResponse.newBuilder().setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    public void sendToServer(CSMRequest request,
                          StreamObserver<CSMResponse> responseObserver){
        try {
            CloudHandler server = new CloudHandler();
            responseObserver.onNext(CSMResponse.newBuilder()
                    .setStatus(server.persist(request.getType(),request.getData())).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
