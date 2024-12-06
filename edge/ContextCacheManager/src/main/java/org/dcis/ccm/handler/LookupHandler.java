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
                if(request.getIdentifier().contains("Situation")) {
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
