package org.dcis.grpc.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class CAMChannel {
    private static volatile CAMChannel instance;
    private final Channel channel;

    private CAMChannel() {
        channel = ManagedChannelBuilder
                .forAddress("0.0.0.0", 8300)
                .usePlaintext()
                .build();
    }

    public static CAMChannel getInstance() {
        if (instance == null) {
            synchronized (CAMChannel.class) {
                if (instance == null) {
                    instance = new CAMChannel();
                }
            }
        }
        return instance;
    }

    public Channel getChannel() {
        return channel;
    }
}
