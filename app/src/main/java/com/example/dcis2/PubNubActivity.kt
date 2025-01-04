package com.example.dcis2
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import com.google.gson.JsonObject
import com.pubnub.api.PubNub
import com.pubnub.api.UserId
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.v2.callbacks.EventListener
import com.pubnub.api.v2.callbacks.StatusListener
import com.pubnub.api.v2.subscriptions.SubscriptionOptions
class PubNubActivity : AppCompatActivity() {

    private lateinit var pubNubManager: PubNubManager
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pubnub)

        textView = findViewById(R.id.textView)

        val config = com.pubnub.api.v2.PNConfiguration.builder(UserId("DieuBangMach"), "sub-c-7d85620f-647f-486c-9a23-cf41747ac989").apply {
            publishKey = "pub-c-e7ffdee8-0e46-42c6-ba0f-23d971d2d21b"
        }

        val pubnub = PubNub.create(config.build())

        // Define the channel you want to subscribe to
        val channel = pubnub.channel("myChannel")

        // Define subscription options if necessary (optional step, depending on SDK capabilities)
        val options = SubscriptionOptions.receivePresenceEvents() // Example option

        // Assuming a conceptual way to explicitly create a subscription
        val subscription = channel.subscription(options)

        // Activate the subscription to start receiving messages
        subscription.subscribe()



        // Define a message
        val myMessage = JsonObject().apply {
            addProperty("msg", "Hello, world")
        }

        // Publishing a message to the provided channel
        channel.publish(myMessage).async { result ->
            result.onFailure { exception ->
                println("Error while publishing")
                exception.printStackTrace()
            }.onSuccess { value ->
                println("Message sent, timetoken: ${value.timetoken}")
            }
        }
        Thread.sleep(4000)



        // Add a listener
        subscription.addListener(object : EventListener {
            override fun message(pubnub: PubNub, result: PNMessageResult) {
                println("Received message ${result.message.asJsonObject}")
            }

            override fun presence(pubnub: PubNub, result: PNPresenceEventResult) {
                // Handle presence
            }
        })

        // Initialize PubNubManager with your PubNub keys and channel name
        pubNubManager = PubNubManager(
            publishKey = "pub-c-e7ffdee8-0e46-42c6-ba0f-23d971d2d21b",
            subscribeKey = "sub-c-7d85620f-647f-486c-9a23-cf41747ac989",
            channelName = "myChannel"
        ) { message ->
            runOnUiThread {
                textView.text = "Received message: $message"
            }
        }

    }
}