package org.dcis.grpc.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class REChannel {
    private static volatile REChannel instance;
    private final Channel channel;

    private REChannel() {
        channel = ManagedChannelBuilder
                .forAddress("0.0.0.0", 8500)
                .usePlaintext()
                .build();
    }

    public static REChannel getInstance() {
        if (instance == null) {
            synchronized (CAMChannel.class) {
                if (instance == null) {
                    instance = new REChannel();
                }
            }
        }
        return instance;
    }

    public Channel getChannel() {
        return channel;
    }
}
