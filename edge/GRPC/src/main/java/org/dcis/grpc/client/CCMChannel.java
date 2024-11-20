package org.dcis.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CCMChannel {
    private volatile CCMChannel instance;
    private final ManagedChannel channel;

    private CCMChannel() {
        channel = ManagedChannelBuilder
                .forAddress("ccm", 8200)
                .usePlaintext()
                .build();
    }

    public CCMChannel getInstance() {
        if (instance == null) {
            synchronized (CCMChannel.class) {
                if (instance == null) {
                    instance = new CCMChannel();
                }
            }
        }
        return instance;
    }

    public ManagedChannel getChannel() {
        return channel;
    }
}
