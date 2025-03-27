package com.example.dcis2
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import com.google.gson.JsonObject
import com.pubnub.api.PubNub
import com.pubnub.api.UserId
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.api.models.consumer.pubsub.objects.PNObjectEventResult
import com.pubnub.api.v2.callbacks.EventListener
import com.pubnub.api.v2.subscriptions.SubscriptionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class PubNubActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private var isConnected = false
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pubnub)

        textView = findViewById(R.id.textView)

        val config = com.pubnub.api.v2.PNConfiguration.builder(UserId("DieuBangMach"), "sub-c-7d85620f-647f-486c-9a23-cf41747ac989").apply {
            publishKey = "pub-c-e7ffdee8-0e46-42c6-ba0f-23d971d2d21b"
        }

        val pubnub = PubNub.create(config.build())

        // Define the channel you want to subscribe to
        val channel = pubnub.channel("edgeNotifications")

        // Define subscription options if necessary (optional step, depending on SDK capabilities)
        val options = SubscriptionOptions.receivePresenceEvents() // Example option

        // Assuming a conceptual way to explicitly create a subscription
        val subscription = channel.subscription(options)

        // Activate the subscription to start receiving messages
        subscription.subscribe()

        // Define a message
        val myMessage = JsonObject().apply {
            addProperty("msg", "Hello,World!")
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
        // Activate the subscription to start receiving events
        subscription.subscribe()

        // Add a listener to the subscription for handling various event types
        subscription.addListener(object : EventListener {
            override fun message(pubnub: PubNub, message: PNMessageResult) {
                // Log or process message
                println("Message: ${message.message}")
                runOnUiThread {
                    textView.text = "testing message: ${message.message.asJsonObject}"
                }
            }
            override fun signal(pubnub: PubNub, signal: PNSignalResult) {
                // Handle signals
                println("Signal: ${signal.message}")
            }

            override fun messageAction(pubnub: PubNub, messageAction: PNMessageActionResult) {
                // Handle message reactions
                println("Message Reaction: ${messageAction.data}")
            }

            override fun file(pubnub: PubNub, file: PNFileEventResult) {
                // Handle file events
                println("File: ${file.file.name}")
            }

            override fun objects(pubnub: PubNub, obj: PNObjectEventResult) {
                // Handle metadata updates
                println("App Context: ${obj}")
            }

            override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {
                // Handle presence updates
                // requires a subscription with presence
                println("Presence: ${presence.uuid} - ${presence.event}")
            }
        })


    }
}