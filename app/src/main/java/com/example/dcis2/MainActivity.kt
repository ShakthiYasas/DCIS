package com.example.dcis2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dcis2.ultility.PreferencesUtils
import com.example.dcis2.ultility.isNetworkAvailable
import com.google.gson.JsonArray
import com.pubnub.api.PubNub
import com.pubnub.api.UserId
import org.dcis.ContextCordinator

class MainActivity : AppCompatActivity() {

    private lateinit var btnScanQR: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("Context Coordinator Health: ${ContextCordinator.health()}")
        PreferencesUtils.resetPreferences(this)
        btnScanQR = findViewById(R.id.btnScanQR)


        // Set click listener on the button
        btnScanQR.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        val configBuilder = com.pubnub.api.v2.PNConfiguration.builder(UserId("DieuBangMach"), "").apply {
            publishKey = ""
        }
        val pubnub = PubNub.create(configBuilder.build())

        val channel = pubnub.channel("myChannel")

        // Define a message
        val myMessage = JsonArray().apply {
            add(32L)
            add(35L)
        }
        channel.publish(
            message = myMessage,
            shouldStore = true,
            ttl = 10,
        ).async { result ->
            result.onFailure { exception ->
                println("Error while publishing")
                exception.printStackTrace()
            }.onSuccess { value ->
                println("Message sent, timetoken: ${value.timetoken}")
            }
        }

    }

}

