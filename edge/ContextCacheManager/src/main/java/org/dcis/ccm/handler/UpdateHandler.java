package org.dcis.ccm.handler;

import org.dcis.ccm.cache.ContextCache;
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

    // Updates a given piece of context information.
    // request: Request containing the identifier and the new piece of context information.
    // returns: Response with the status.
    public CCMResponse updateContext (CCMRequest request) {
        ContextCache cache = ContextCache.getInstance();
        cache.add(request.getIdentifier(), request.getData());
        return CCMResponse.newBuilder().setStatus(200).build();
    }
}
