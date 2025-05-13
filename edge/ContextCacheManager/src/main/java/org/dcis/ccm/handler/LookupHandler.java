package org.dcis.ccm.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import org.dcis.ccm.cache.ContextCache;
import com.google.protobuf.util.JsonFormat;

import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;
import org.dcis.cim.proto.SituationDescription;

public final class LookupHandler {
    private static LookupHandler instance;

    private LookupHandler() {}

    public static synchronized LookupHandler getInstance() {
        if(instance == null) {
            instance = new LookupHandler();
        }
        return instance;
    }

    // Looks up whether a given piece of context information is already cached using a key.
    // request: Key of the context information and type of context.
    // returns: Status. 200 is cached, 404 if not cached or stale, 400 if the lookup operation requested is invalid,
    // or 500 in case of an internal error.
    public CCMResponse lookUp (CCMRequest request) {
        ContextCache cache = ContextCache.getInstance();
        switch(request.getOperation()) {
            case CCMRequest.OPERATION.LOOKUP -> {
                if (cache.lookup(request.getIdentifier()))
                    return CCMResponse.newBuilder().setStatus(200).build();
                return CCMResponse.newBuilder().setStatus(404).build();
            }
            case CCMRequest.OPERATION.READ -> {
                Object item = cache.get(request.getIdentifier());
                if(item == null)
                    return CCMResponse.newBuilder().setStatus(404).build();

                // Transformations: Maps JSON to specific protos.
                if((request.getIdentifier().toLowerCase()).contains("situation")) {
                    try {
                        SituationDescription.Builder situBuilder = SituationDescription.newBuilder();
                        JsonFormat.parser().ignoringUnknownFields().merge((String) item, situBuilder);
                        return CCMResponse.newBuilder()
                                .setSituation(situBuilder)
                                .setStatus(200).build();
                    } catch (InvalidProtocolBufferException e) {
                        return CCMResponse.newBuilder()
                                .setBody(e.getMessage())
                                .setStatus(500).build();
                    }
                }
                else
                    return CCMResponse.newBuilder()
                            .setBody((String) item).setStatus(200).build();
            }
            default -> {
                return CCMResponse.newBuilder().setStatus(400).build();
            }
        }
    }
}
