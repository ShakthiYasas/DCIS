package org.dcis.ccm.handler;

import org.dcis.ccm.cache.ContextCache;
import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;

public final class EvictionHandler {
    private static EvictionHandler instance;

    private EvictionHandler() {}

    public static synchronized EvictionHandler getInstance() {
        if(instance == null) {
            instance = new EvictionHandler();
        }
        return instance;
    }

    public CCMResponse evictContext (CCMRequest request) {
        ContextCache cache = ContextCache.getInstance();
        switch(request.getOperation()) {
            case CCMRequest.OPERATION.EVICT ->
                cache.addGhost(request.getIdentifier());
            case CCMRequest.OPERATION.FORCEEVICT ->
                cache.remove(request.getIdentifier());
            default -> {
                return CCMResponse.newBuilder().setStatus(400).build();
            }
        }
        return CCMResponse.newBuilder().setStatus(200).build();
    }
}
