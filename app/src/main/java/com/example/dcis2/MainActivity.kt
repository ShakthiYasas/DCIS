package com.example.dcis2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dcis2.ultility.isNetworkAvailable
import com.google.gson.JsonArray
import com.pubnub.api.PubNub
import com.pubnub.api.UserId

class MainActivity : AppCompatActivity() {

    private lateinit var btnScanQR: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnScanQR = findViewById(R.id.btnScanQR)

        // Check for internet connectivity
        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show()
            // Optional: Handle no internet connection scenario, e.g., show a retry button
        } else {
            Toast.makeText(this, "Internet connection detected", Toast.LENGTH_LONG).show()
        }
        // Set click listener on the button
        btnScanQR.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        }


        val configBuilder = com.pubnub.api.v2.PNConfiguration.builder(UserId("DieuBangMach"), "sub-c-7d85620f-647f-486c-9a23-cf41747ac989").apply {
            publishKey = "pub-c-e7ffdee8-0e46-42c6-ba0f-23d971d2d21b"
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

