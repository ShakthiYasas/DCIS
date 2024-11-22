package org.dcis.grpc.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class CCMChannel {
    private static volatile CCMChannel instance;
    private final Channel channel;

    private CCMChannel() {
        channel = ManagedChannelBuilder
                .forAddress("ccm", 8200)
                .usePlaintext()
                .build();
    }

    public static CCMChannel getInstance() {
        if (instance == null) {
            synchronized (CCMChannel.class) {
                if (instance == null) {
                    instance = new CCMChannel();
                }
            }
        }
        return instance;
    }

    public Channel getChannel() {
        return channel;
    }
}
