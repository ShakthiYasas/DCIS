package org.dcis.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CIMChannel {
    private volatile CIMChannel instance;
    private final ManagedChannel channel;

    private CIMChannel() {
        channel = ManagedChannelBuilder
                .forAddress("cim", 8100)
                .usePlaintext()
                .build();
    }

    public CIMChannel getInstance() {
        if (instance == null) {
            synchronized (CIMChannel.class) {
                if (instance == null) {
                    instance = new CIMChannel();
                }
            }
        }
        return instance;
    }

    public ManagedChannel getChannel() {
        return channel;
    }
}
