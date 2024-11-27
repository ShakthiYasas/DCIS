package org.dcis.cam.server;

import io.grpc.stub.StreamObserver;

import org.dcis.cam.manager.CPManager;
import org.dcis.cam.proto.CAMRequest;
import org.dcis.cam.proto.CAMResponse;
import org.dcis.cam.proto.CAMServiceGrpc;

import org.dcis.cam.manager.ContextManager;

public class CAMServiceImpl extends CAMServiceGrpc.CAMServiceImplBase {
    public void setContext(CAMRequest request,
                           StreamObserver<CAMResponse> responseObserver) {
        try {
            ContextManager.getInstance().acquire(request);
            responseObserver.onNext(CAMResponse.newBuilder().setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    public void getFromProvider(CAMRequest request,
                           StreamObserver<CAMResponse> responseObserver) {
        try {
            responseObserver.onNext(CAMResponse.newBuilder().setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    public void verifyProvider(CAMRequest request,
                           StreamObserver<CAMResponse> responseObserver) {
        try {
            responseObserver.onNext(CPManager.getInstance()
                    .verifyProvider(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
