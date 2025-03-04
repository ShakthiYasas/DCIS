package org.dcis.grpc.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class CIMChannel {
    private static volatile CIMChannel instance;
    private final Channel channel;

    private CIMChannel() {
        channel = ManagedChannelBuilder
                .forAddress("0.0.0.0", 8100)
                .usePlaintext()
                .build();
    }

    public static CIMChannel getInstance() {
        if (instance == null) {
            synchronized (CIMChannel.class) {
                if (instance == null) {
                    instance = new CIMChannel();
                }
            }
        }
        return instance;
    }

    public Channel getChannel() {
        return channel;
    }
}
