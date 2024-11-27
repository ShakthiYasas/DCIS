package org.dcis.cam.manager;

import org.dcis.cam.proto.CAMRequest;

public final class ContextManager {

    private static ContextManager instance;

    private ContextManager() {}

    public static synchronized ContextManager getInstance() {
        if(instance == null) {
            instance = new ContextManager();
        }
        return instance;
    }

    public void acquire(CAMRequest request) {
        switch(request.getDataType()) {
            case CAMRequest.TYPE.LOCATION -> {
                // Send to CIM
            }
            case CAMRequest.TYPE.HEALTH -> {
                // Send to CIM
            }
            case CAMRequest.TYPE.ANIMAL -> {
                // Cache
            }
        }
    }

}
