package org.dcis.cam.server;

import io.grpc.stub.StreamObserver;

import org.dcis.cam.manager.CPManager;
import org.dcis.cam.proto.CAMRequest;
import org.dcis.cam.proto.CAMResponse;
import org.dcis.cam.proto.CAMServiceGrpc;

import org.dcis.cam.manager.ContextManager;

public class CAMServiceImpl extends CAMServiceGrpc.CAMServiceImplBase {
    // Front end push context information directly.
    // Response: Only the status of the operation.
    public void setContext(CAMRequest request,
                           StreamObserver<CAMResponse> responseObserver) {
        try {
            ContextManager.getInstance().acquire(request);
            responseObserver.onNext(CAMResponse.newBuilder()
                    .setStatus(200).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Front end detects nearing an enclosure and request pre-fetching.
    // Response: Context-aware message about what to expect at the enclosure.
    public void getDescription(CAMRequest request,
                               StreamObserver<CAMResponse> responseObserver) {
        try {
            responseObserver.onNext(CPManager.getInstance()
                    .getProvider(request.getIdentifier()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    // Front end detects a BLE device that is broadcasting context and requests to verify it with prefetched metadata.
    // Response: The status of the operation. 200 if verified, 404 if unauthorised, 400 if no such BLE device.
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

    // Retrieves the latest context about an entity persisted in the Cloud Server.
    // Response: JSON object containing the probability that an animal is currently active.
    public void getBackUpContext(CAMRequest request,
                                StreamObserver<CAMResponse> responseObserver) {
        try {
            responseObserver.onNext(CPManager.getInstance()
                    .getBackUpContext(request.getIdentifier()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
