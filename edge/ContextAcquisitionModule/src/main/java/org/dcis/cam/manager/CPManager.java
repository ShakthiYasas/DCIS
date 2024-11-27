package org.dcis.cam.manager;

import org.json.JSONObject;

import org.dcis.cam.invoker.CPInvoker;
import org.dcis.grpc.client.CCMChannel;

import org.dcis.cam.proto.CAMRequest;
import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;
import org.dcis.cam.proto.CAMResponse;
import org.dcis.ccm.proto.CCMServiceGrpc;

public final class CPManager {

    private static CPManager instance;

    private CPManager() {}

    public static synchronized CPManager getInstance() {
        if(instance == null) {
            instance = new CPManager();
        }
        return instance;
    }

    // Fetches BLE description from Cloud and caches.
    // tag: enclosure_tag (e.g., lion_enc)
    // returns: None
    public void getProvider(String tag) throws Exception {
        CPInvoker invoker = new CPInvoker();
        cacheProvider(invoker.fetch(tag));
    }

    private void cacheProvider(String description) {
        CCMServiceGrpc.CCMServiceFutureStub stub =
                CCMServiceGrpc.newFutureStub(CCMChannel.getInstance().getChannel());
        stub.storeInCache(CCMRequest.newBuilder()
                        .setIdentifier("enclosure")
                        .setData(description)
                        .build());
    }

    // Verifies a discovered BLE with cached metadata.
    // identifier: Hash key of the BLE device.
    public CAMResponse verifyProvider(CAMRequest request) {
        CCMServiceGrpc.CCMServiceBlockingStub stub =
                CCMServiceGrpc.newBlockingStub(CCMChannel.getInstance().getChannel());
        CCMResponse response = stub.lookupCache(CCMRequest.newBuilder()
                        .setIdentifier("enclosure")
                        .setOperation(CCMRequest.OPERATION.READ)
                        .build());

        if(response.getStatus() == 200) {
            JSONObject meta = new JSONObject(response.getBody());
            if(meta.getString("hash").equals(request.getIdentifier())) {
                return CAMResponse.newBuilder()
                        .setStatus(200).build();
            }
            return CAMResponse.newBuilder()
                    .setStatus(400).build();
        }

        return CAMResponse.newBuilder()
                .setStatus(response.getStatus()).build();
    }

}
