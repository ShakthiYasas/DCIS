package com.example.dcis2

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dcis2.R
import com.example.dcis2.ScanQRActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var btnScanQR: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        progressBar = findViewById(R.id.progressBar)
        btnScanQR = findViewById(R.id.btnScanQR)

        // Simulate initialization of background services with a progress update
        initializeServices()

        // Set click listener on the button
        btnScanQR.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish() // Optionally close the splash screen
        }
    }

    private fun initializeServices() {
        // Example: Simulate a loading progress
        val handler = Handler(Looper.getMainLooper())
        var progressStatus = 0
        val delay = 50L // Adjust the delay as per your loading logic

        handler.post(object : Runnable {
            override fun run() {
                if (progressStatus < 100) {
                    progressStatus += 5
                    progressBar.progress = progressStatus
                    handler.postDelayed(this, delay)
                } else {
                    // When initialization completes, show the button
                    btnScanQR.visibility = View.VISIBLE
                }
            }
        })
    }
}
