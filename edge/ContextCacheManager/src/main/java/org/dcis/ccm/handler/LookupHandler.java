package org.dcis.ccm.handler;

import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;

public final class LookupHandler {
    private static LookupHandler instance;

    private LookupHandler() {}

    public static synchronized LookupHandler getInstance() {
        if(instance == null) {
            instance = new LookupHandler();
        }
        return instance;
    }

    public CCMResponse lookUp (CCMRequest request) {
        return CCMResponse.newBuilder().build();
    }
}
