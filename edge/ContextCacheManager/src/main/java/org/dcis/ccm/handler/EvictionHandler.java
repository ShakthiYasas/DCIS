package org.dcis.ccm.handler;

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
        return CCMResponse.newBuilder().build();
    }
}
