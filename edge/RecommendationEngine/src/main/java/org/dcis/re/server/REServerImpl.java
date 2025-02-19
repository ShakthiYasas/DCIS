package org.dcis.re.server;

import io.grpc.stub.StreamObserver;
import org.dcis.re.proto.RERequest;
import org.dcis.re.proto.REResponse;
import org.dcis.re.proto.REServiceGrpc;

public class REServerImpl extends REServiceGrpc.REServiceImplBase{
    public void getRoute(RERequest request,
                          StreamObserver<REResponse> responseObserver){
        try {
            responseObserver.onNext(REResponse.newBuilder().setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
