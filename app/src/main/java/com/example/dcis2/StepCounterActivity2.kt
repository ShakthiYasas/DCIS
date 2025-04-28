package com.example.dcis2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dcis2.serivce.stepCounterService
import org.dcis.ContextCordinator
import org.json.JSONObject

class StepCounterActivity2:  AppCompatActivity() {
    private lateinit var stepCountTextView: TextView
    private lateinit var startStepCountButton: Button
    private lateinit var stopStepCountButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)

        stepCountTextView = findViewById(R.id.stepCountTextView)
        startStepCountButton = findViewById(R.id.startStepCountButton)
        stopStepCountButton = findViewById(R.id.stopStepCountButton)
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Display the last saved step count
        updateStepCountDisplay()

        startStepCountButton.setOnClickListener {
            checkPermissionAndStartService()
        }

        stopStepCountButton.setOnClickListener {
            stopStepCounterService()
        }
    }

    private fun updateStepCountDisplay() {
        val stepCount = sharedPreferences.getInt("step_count", 0)
        stepCountTextView.text = "Steps: $stepCount"

        // Send step count to ContextCoordinator
        val healthData = JSONObject().apply {
            put("step_count", stepCount)
            put("timestamp", System.currentTimeMillis() / 1000)
        }
        ContextCordinator.setHealth(healthData)
    }

    private fun checkPermissionAndStartService() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_REQUEST_CODE
            )
        } else {
            startStepCounterService()
        }
    }

    private fun startStepCounterService() {
        val serviceIntent = Intent(this, stepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun stopStepCounterService() {
        val serviceIntent = Intent(this, stepCounterService::class.java)
        stopService(serviceIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startStepCounterService()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateStepCountDisplay()
    }
}