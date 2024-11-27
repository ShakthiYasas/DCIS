package org.dcis.ccm.handler;

import org.dcis.ccm.cache.ContextCache;
import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;

public final class CachingHandler {
    private static CachingHandler instance;

    private CachingHandler() {}

    public static synchronized CachingHandler getInstance() {
        if(instance == null) {
            instance = new CachingHandler();
        }
        return instance;
    }

    public CCMResponse cacheContext (CCMRequest request) {
        ContextCache cache = ContextCache.getInstance();
        cache.add(request.getIdentifier(), request.getData());
        return CCMResponse.newBuilder().setStatus(200).build();
    }
}
