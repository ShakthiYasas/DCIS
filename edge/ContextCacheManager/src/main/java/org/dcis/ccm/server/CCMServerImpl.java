package org.dcis.ccm.server;

import io.grpc.stub.StreamObserver;

import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;
import org.dcis.ccm.proto.CCMServiceGrpc;

import org.dcis.ccm.handler.UpdateHandler;
import org.dcis.ccm.handler.LookupHandler;
import org.dcis.ccm.handler.CachingHandler;
import org.dcis.ccm.handler.EvictionHandler;

public class CCMServerImpl extends CCMServiceGrpc.CCMServiceImplBase {
    public void lookupCache(CCMRequest request,
                            StreamObserver<CCMResponse> responseObserver){
        try {
            responseObserver.onNext(LookupHandler.getInstance()
                    .lookUp(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    public void storeInCache(CCMRequest request,
                            StreamObserver<CCMResponse> responseObserver){
        try {
            responseObserver.onNext(CachingHandler.getInstance()
                    .cacheContext(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    public void updateCache(CCMRequest request,
                             StreamObserver<CCMResponse> responseObserver){
        try {
            responseObserver.onNext(UpdateHandler.getInstance()
                    .updateContext(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    public void deleteInCache(CCMRequest request,
                             StreamObserver<CCMResponse> responseObserver){
        try {
            responseObserver.onNext(EvictionHandler.getInstance()
                    .evictContext(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
