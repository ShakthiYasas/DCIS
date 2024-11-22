package org.dcis.ccm.handler;

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
        return CCMResponse.newBuilder().build();
    }
}
