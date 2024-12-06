package org.dcis.csm.handler;

import org.jetbrains.annotations.NotNull;

import org.json.JSONObject;
import java.util.Properties;
import java.io.FileInputStream;

import com.pubnub.api.UserId;
import com.pubnub.api.java.PubNub;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.java.v2.PNConfiguration;
import com.pubnub.api.java.v2.entities.Channel;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.java.v2.callbacks.StatusListener;

public final class PubNubWrapper {

    private static PubNub pubNub;
    private static Channel channel;
    private static PubNubWrapper instance;

    private PubNubWrapper() throws Exception {
        Properties appProps = new Properties();
        appProps.load(new FileInputStream("pubsub.properties"));

        PNConfiguration.Builder configBuilder =
                PNConfiguration.builder(new UserId(appProps.getProperty("userId")),
                        appProps.getProperty("subscribeKey"));
        configBuilder.publishKey(appProps.getProperty("publishKey"));
        pubNub = PubNub.create(configBuilder.build());
        channel = pubNub.channel(appProps.getProperty("channelName"));
    }

    public static synchronized PubNubWrapper getInstance() throws Exception {
        if(instance == null) {
            instance = new PubNubWrapper();
        }
        return instance;
    }

    public void sendToApp(String message) {
        JSONObject jsonMessage = new JSONObject(message);
        channel.publish(jsonMessage);
    }
}
