package com.example.dcis2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dcis2.ultility.isNetworkAvailable

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
            val intent = Intent(this, HealthSensorActivity::class.java)
            startActivity(intent)
            finish() // Optionally close the splash screen
        }
    }

}

