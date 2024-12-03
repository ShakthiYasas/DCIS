package com.example.dcis2.utility

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast

object HealthSensorUtils {

    private const val HEART_RATE_SENSOR_TYPE = Sensor.TYPE_HEART_RATE
    private const val STEP_SENSOR_TYPE = Sensor.TYPE_STEP_COUNTER

    // Simulate heart rate if sensor is not available
    fun getHeartRate(context: Context): Int? {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val heartRateSensor = sensorManager.getDefaultSensor(HEART_RATE_SENSOR_TYPE)

        if (heartRateSensor != null) {
            // Implement the logic to fetch heart rate sensor data (if available)
            // Here we can listen to the sensor if available, for now just return the value
            return 75 // Placeholder for actual sensor data (can be fetched using a listener)
        } else {
            // Return mock data if heart rate sensor is not available
            return null
        }
    }

    // Simulate step count if sensor is not available
    fun getStepCount(context: Context): Int? {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(STEP_SENSOR_TYPE)

        if (stepSensor != null) {
            // Implement the logic to fetch step count data (if available)
            // Here we can listen to the sensor if available
            return 1000 // Placeholder for actual step data
        } else {
            return null
        }
    }
}
