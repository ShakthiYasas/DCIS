package org.dcis.ccm.handler;

import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;

public final class UpdateHandler {
    private static UpdateHandler instance;

    private UpdateHandler() {}

    public static synchronized UpdateHandler getInstance() {
        if(instance == null) {
            instance = new UpdateHandler();
        }
        return instance;
    }

    public CCMResponse updateContext (CCMRequest request) {
        return CCMResponse.newBuilder().build();
    }
}
