package org.dcis.csm.handler;

import org.json.JSONObject;
import java.util.Properties;
import java.io.FileInputStream;

import com.pubnub.api.UserId;
import com.pubnub.api.java.PubNub;
import com.pubnub.api.java.v2.PNConfiguration;
import com.pubnub.api.java.v2.entities.Channel;

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

    // Sends data to the front end application, asynchronously.
    // message: The information (a String converted JSON object) that needs to be sent.
    // returns: None. Send and forget.
    public void sendToApp(String message) {
        JSONObject jsonMessage = new JSONObject(message);
        channel.publish(jsonMessage);
    }
}
