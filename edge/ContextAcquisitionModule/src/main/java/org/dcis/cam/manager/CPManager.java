package org.dcis.cam.manager;

import org.dcis.cam.invoker.GenAIInvoker;
import org.dcis.cim.proto.*;
import org.dcis.csm.proto.CSMRequest;
import org.dcis.csm.proto.CSMServiceGrpc;
import org.dcis.grpc.client.CSMChannel;
import org.json.JSONObject;
import com.google.protobuf.util.JsonFormat;

import org.dcis.grpc.client.CIMChannel;

import org.dcis.cam.invoker.CPInvoker;
import org.dcis.grpc.client.CCMChannel;

import org.dcis.cam.proto.CAMRequest;
import org.dcis.ccm.proto.CCMRequest;
import org.dcis.ccm.proto.CCMResponse;
import org.dcis.cam.proto.CAMResponse;
import org.dcis.ccm.proto.CCMServiceGrpc;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public final class CPManager {

    private static CPManager instance;
    private final ExecutorService executor;

    private CPManager() {
        executor = Executors.newSingleThreadExecutor();
    }

    public static synchronized CPManager getInstance() {
        if(instance == null) {
            instance = new CPManager();
        }
        return instance;
    }

    // Fetches BLE description from Cloud and caches.
    // tag: enclosure_tag (e.g., lion_enc)
    // returns: context-aware message relevant to the event.
    public CAMResponse getProvider(String tag) throws Exception {
        String animal = tag.split("_")[0];

        CPInvoker invoker = new CPInvoker();
        String response = invoker.fetch(animal);
        executor.execute(() -> cacheProvider(response));

        JSONObject json = new JSONObject(response);
        return getProbableContext(animal, json.getJSONObject("situation"));
    }

    // Fetches the backup context from the Cloud Server.
    // type: Entity type of which the context needs to be fetched.
    // tag: Identifier of the entity. For enclosures its, e.g., lion_enc.
    // returns: JSON response containing the latest context.
    public CAMResponse getBackUpContext(String tag)
            throws Exception {
        CPInvoker invoker = new CPInvoker();
        String response = invoker.fetchBackup(tag);
        if(response == null)
            return CAMResponse.newBuilder().setStatus(400).build();
        return CAMResponse.newBuilder()
                .setBody(response)
                .setStatus(200).build();
    }

    // Storing context in cache.
    private void cacheProvider(String description) {
        CCMServiceGrpc.CCMServiceFutureStub stub =
                CCMServiceGrpc.newFutureStub(CCMChannel.getInstance().getChannel());
        stub.storeInCache(CCMRequest.newBuilder()
                        .setIdentifier("enclosure")
                        .setData(description)
                        .build());
    }

    // Calculate the probability of a situation using CST.
    // animal: Name of the animal of which the situation is inferred.
    // situFunc: The defined situation model according to CST.
    private CAMResponse getProbableContext(String animal, JSONObject situFunc)
            throws Exception {
        CIMServiceGrpc.CIMServiceBlockingStub stub =
                CIMServiceGrpc.newBlockingStub(CIMChannel.getInstance().getChannel());

        SituationDescription.Builder situBuilder = SituationDescription.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(situFunc.toString(), situBuilder);

        CIMResponse response = stub.infer(CIMRequest.newBuilder()
                .setDescription(situBuilder)
                .setData(getContext()).build());

        executor.execute(() -> {
                CSMServiceGrpc.CSMServiceFutureStub fu_stub =
                            CSMServiceGrpc.newFutureStub(CSMChannel.getInstance().getChannel());
                JSONObject data = new JSONObject();
                data.put("enclosure", animal+"_enc");
                data.put("context", response.getProb());
                fu_stub.sendToServer(CSMRequest.newBuilder()
                                .setType(CSMRequest.TYPE.BACKUP)
                                .setData(data.toString()).build());
            }
        );

        return CAMResponse.newBuilder()
                .setBody(GenAIInvoker.getInstance()
                        .generateNotification(animal, response.getProb()))
                .setStatus(200).build();
    }

    // Retrieve the weather data from provider.
    private String getContext() throws Exception {
        CCMServiceGrpc.CCMServiceBlockingStub stub =
                CCMServiceGrpc.newBlockingStub(CCMChannel.getInstance().getChannel());
        CCMResponse response = stub.lookupCache(CCMRequest.newBuilder()
                .setOperation(CCMRequest.OPERATION.READ)
                .setIdentifier("weather")
                .build());
        if(response.getStatus() == 200) return response.getBody();
        else {
            CPInvoker invoker = new CPInvoker();
            JSONObject weather = new JSONObject(invoker.fetch("weather"));
            JSONObject context = weather.getJSONObject("main");

            executor.execute(() -> {
                CCMServiceGrpc.CCMServiceBlockingStub future =
                        CCMServiceGrpc.newBlockingStub(CCMChannel.getInstance().getChannel());
                future.updateCache(CCMRequest.newBuilder()
                        .setIdentifier("weather")
                        .setData(context.toString()).build());
            });

            context.put("hour", LocalDateTime.now().getHour());
            return context.toString();
        }
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
                // Setting the stationary time assessment.
                executor.execute(() -> {
                    CIMServiceGrpc.CIMServiceBlockingStub cache_stub =
                            CIMServiceGrpc.newBlockingStub(CIMChannel.getInstance().getChannel());
                    cache_stub.setQuery(SiddhiRequest.newBuilder()
                                    .setDomain(SiddhiRequest.DOMAIN.LOCATION)
                                    .setName(meta.getString("tag")).build());
                });

                return CAMResponse.newBuilder()
                        .setBody(meta.getString("session"))
                        .setStatus(200).build();
            }
            return CAMResponse.newBuilder()
                    .setStatus(400).build();
        }

        return CAMResponse.newBuilder()
                .setStatus(response.getStatus()).build();
    }

}
