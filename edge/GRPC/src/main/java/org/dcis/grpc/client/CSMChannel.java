package org.dcis.grpc.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class CSMChannel {
    private static volatile CSMChannel instance;
    private final Channel channel;

    private CSMChannel() {
        channel = ManagedChannelBuilder
                .forAddress("0.0.0.0", 8400)
                .usePlaintext()
                .build();
    }

    public static CSMChannel getInstance() {
        if (instance == null) {
            synchronized (CSMChannel.class) {
                if (instance == null) {
                    instance = new CSMChannel();
                }
            }
        }
        return instance;
    }

    public Channel getChannel() {
        return channel;
    }
}
