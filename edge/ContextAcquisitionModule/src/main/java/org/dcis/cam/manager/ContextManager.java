package org.dcis.cam.manager;

import org.dcis.cam.proto.CAMRequest;
import org.dcis.ccm.proto.CCMRequest;
import org.dcis.cim.proto.SiddhiRequest;
import org.dcis.cim.proto.CIMServiceGrpc;
import org.dcis.ccm.proto.CCMServiceGrpc;

import org.dcis.grpc.client.CIMChannel;
import org.dcis.grpc.client.CCMChannel;

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
        Object stub;
        switch(request.getDataType()) {
            case CAMRequest.TYPE.LOCATION -> {
                stub = CIMServiceGrpc.newFutureStub(CIMChannel.getInstance().getChannel());
                ((CIMServiceGrpc.CIMServiceFutureStub)stub)
                        .addEvent(SiddhiRequest.newBuilder()
                        .setDomain(SiddhiRequest.DOMAIN.LOCATION)
                        .setJson(request.getData())
                        .build());
            }
            case CAMRequest.TYPE.HEALTH -> {
                stub = CIMServiceGrpc.newFutureStub(CIMChannel.getInstance().getChannel());
                ((CIMServiceGrpc.CIMServiceFutureStub)stub)
                        .addEvent(SiddhiRequest.newBuilder()
                            .setDomain(SiddhiRequest.DOMAIN.HEALTH)
                            .setJson(request.getData())
                        .build());
            }
            case CAMRequest.TYPE.ANIMAL -> {
                 stub = CCMServiceGrpc.newFutureStub(CCMChannel.getInstance().getChannel());
                ((CCMServiceGrpc.CCMServiceFutureStub)stub)
                        .updateCache(CCMRequest.newBuilder()
                                .setIdentifier("animal")
                                .setData(request.getData())
                        .build());
            }
        }
    }

}
