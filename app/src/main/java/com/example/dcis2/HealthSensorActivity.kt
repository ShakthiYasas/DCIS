package com.example.dcis2

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dcis2.utility.HealthSensorUtils


class HealthSensorActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private lateinit var heartRateTextView: TextView
    private lateinit var stepCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_sensor)
        // Initialize UI elements
        heartRateTextView = findViewById(R.id.heartRateTextView)
        stepCountTextView = findViewById(R.id.stepCountTextView)

        // Check if health sensors are available and fetch data
        fetchHealthData()
        // Initialize SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Get Heart Rate Sensor
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        if (heartRateSensor == null) {
            // Device doesn't have a heart rate sensor
            println("Heart Rate Sensor not available.")
        }
        // Check and request location permissions



    }
    private fun fetchHealthData() {
        // Check if heart rate sensor is available and retrieve data
        val heartRate = HealthSensorUtils.getHeartRate(this)
        if (heartRate != null) {
            heartRateTextView.text = "Heart Rate: $heartRate BPM"
        } else {
            // Use mock value for heart rate if not available
            heartRateTextView.text = "Heart Rate: 80 BPM (Mock Data)"
        }

        // Check if step count sensor is available and retrieve data
        val stepCount = HealthSensorUtils.getStepCount(this)
        if (stepCount != null) {
            stepCountTextView.text = "Step Count: $stepCount steps"
        } else {
            stepCountTextView.text = "Step Count: Not available"
        }
    }


    override fun onResume() {
        super.onResume()
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_HEART_RATE) {
                val heartRate = it.values[0]
                println("Heart Rate: $heartRate")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle sensor accuracy changes if needed
    }
}
