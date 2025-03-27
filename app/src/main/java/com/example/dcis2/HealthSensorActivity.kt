package com.example.dcis2

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dcis2.utility.HealthSensorUtils
import org.dcis.ContextCordinator
import org.json.JSONObject



class HealthSensorActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private lateinit var heartRateTextView: TextView
    private lateinit var stepCountTextView: TextView
    private lateinit var geofenceButton: Button
    private var stepCounterSensor: Sensor? = null
    private var totalSteps = 0
    private var previousTotalSteps = 0
    private lateinit var stepsTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_sensor)
        // Initialize UI elements
        heartRateTextView = findViewById(R.id.heartRateTextView)
        stepsTextView = findViewById(R.id.stepsTextView)

        stepCountTextView = findViewById(R.id.stepsTextView)

        geofenceButton = findViewById(R.id.geofenceButton)
        // Check if health sensors are available and fetch data
        fetchHealthData()
        // Initialize SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        // Get Heart Rate Sensor
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        if (heartRateSensor == null) {
            // Device doesn't have a heart rate sensor
            println("Heart Rate Sensor not available.")
            stepsTextView.text = "Steps: 0" // Example

        }
        if (stepCounterSensor == null) {
            // Step counter sensor is not available on this device
            Log.e("StepCounter", "Step counter sensor not available")
            stepsTextView.text = "Step counter sensor not available"
        } else {
            // Load previous step count from SharedPreferences
            loadData()
        }
//        stepsTextView = findViewById(R.id.stepsTextView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Set up button click listener
        geofenceButton.setOnClickListener {
            val intent = Intent(this, GeoFenceActivity::class.java)
            startActivity(intent)
        }


    }

    private fun fetchHealthData() {
        // Check if heart rate sensor is available and retrieve data
        val heartRate = HealthSensorUtils.getHeartRate(this)
        if (heartRate != null) {
            heartRateTextView.text = "Heart Rate: 75 BPM"
        } else {
            // Use mock value for heart rate if not available
            heartRateTextView.text = "Heart Rate: 75 BPM"
        }

        // Check if step count sensor is available and retrieve data
        val stepCount = HealthSensorUtils.getStepCount(this)
        if (stepCount != null) {
            stepCountTextView.text = "Step Count: 1000 steps"
        } else {
            stepCountTextView.text = "Step Count: 1000 steps"
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
        stepCounterSensor?.let {
            sensorManager.unregisterListener(this, it)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_HEART_RATE) {
                val heartRate = it.values[0]
                println("Heart Rate: $heartRate")
            }
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                totalSteps = event.values[0].toInt()
                val currentSteps = totalSteps - previousTotalSteps
                stepsTextView.text = "Steps: $currentSteps"
                Log.d("StepCounter", "Total Steps: $totalSteps")
                Log.d("StepCounter", "Current Steps: $currentSteps")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle sensor accuracy changes if needed
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("previousTotalSteps", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        previousTotalSteps = sharedPreferences.getInt("previousTotalSteps", 0)
    }

    override fun onDestroy() {
        saveData()
        super.onDestroy()
    }




    private fun determineAudience(profileData: JSONObject): String {
        val numberOfAdults = profileData.optInt("Number of Adults", 0)
        val numberOfChildren = profileData.optInt("Number of Children", 0)

        return when {
            numberOfAdults > numberOfChildren -> "adult"
            else -> "kids" // Prioritize kids if equal or more kids
        }
    }



}