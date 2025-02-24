package org.dcis.csm.server;

import io.grpc.stub.StreamObserver;
import org.dcis.csm.handler.CloudHandler;
import org.dcis.csm.handler.PubNubWrapper;
import org.dcis.csm.proto.CSMRequest;
import org.dcis.csm.proto.CSMResponse;
import org.dcis.csm.proto.CSMServiceGrpc;

public class CSMServerImpl extends CSMServiceGrpc.CSMServiceImplBase{

    // Sends given message to the Front End application to be displayed as a notification.
    // Response: None. Status only.
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

    // Sends given piece of information to the Server to persist or backup.
    // Response: None. Status only.
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
