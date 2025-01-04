// PubNubManager.kt
package com.example.dcis2

import android.net.http.UrlRequest
import com.pubnub.api.PubNub
import com.pubnub.api.UserId
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.v2.PNConfiguration
import org.json.JSONObject


class PubNubManager(
    private var publishKey: String,
    private var subscribeKey: String,
    private val channelName: String,
    private val messageListener: (JSONObject) -> Unit
) {

    private var pubnub: PubNub? = null

    init {
        initPubNub()
    }
    private fun initPubNub() {
        val config = PNConfiguration.builder(UserId("DieuBangMach"), "sub-c-7d85620f-647f-486c-9a23-cf41747ac989") {
            publishKey = "pub-c-e7ffdee8-0e46-42c6-ba0f-23d971d2d21b"
        }
        val pubnub = PubNub.create(config.build())
        println("PubNub Init Success")

        subscribeToChannel()
    }


//    fun destroy() {
//        pubnub?.unsubscribe()?.channel(listOf(channelName))?.execute()
//        pubnub?.destroy()
//        pubnub = null
//    }
    private fun subscribeToChannel() {
        pubnub?.let {
            it.addListener(object : SubscribeCallback() {
                override fun status(pubnub: PubNub, pnStatus: PNStatus) {
                    if (pnStatus.category == PNStatusCategory.PNConnectedCategory) {
                        println("PubNub Connected")
                    }
                }

                override fun message(pubnub: PubNub, pnMessageResult: PNMessageResult) {
                    try {
                        val message = pnMessageResult.message
                        if (message is JSONObject) {
                            messageListener(message)
                        } else {
                            println("Received non-JSON message: $message")
                        }
                    } catch (e: Exception) {
                        println("Error processing message: ${e.message}")
                    }
                }

                override fun presence(pubnub: PubNub, pnPresenceEventResult: PNPresenceEventResult) {
                    // Handle presence events if needed
                }
            })
        }
    }
//    fun publishMessage(message: JSONObject) {
//        pubnub?.publish()?.channel(channelName)?.message(message)?.async(object : PNCallback<PNPublishResult> {
//            fun onResponse(result: PNPublishResult, status: PNStatus) {
//                if (status.isError) {
//                    println("Error publishing message: ${status.errorData.message}")
//                } else {
//                    println("Message published successfully: $result")
//                }
//            }
//        })
//    }


}