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
    // Look up a piece of context information in cache memory.
    // Response: Status of the lookup operation.
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

    // Store a piece of context or data in the cache memory.
    // Response: Status of the caching operation.
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

    // Update a piece of context in cache memory.
    // Response: Status of the update operation.
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

    // Delete a piece of context or data in cache.
    // Force evict removes immediately. Evict operations will put the item in ghost cache.
    // Response: Status of the eviction operation.
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
