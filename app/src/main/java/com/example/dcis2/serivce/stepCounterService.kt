package com.example.dcis2.serivce

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.dcis2.R
import com.example.dcis2.MainActivity
import org.json.JSONObject

class stepCounterService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var initialStepCount = -1
    private var currentStepCount = 0
    private val NOTIFICATION_ID = 1234
    private val CHANNEL_ID = "step_counter_channel"

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification(0))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Step counter sensor registered")
        } ?: run {
            Log.e(TAG, "Step counter sensor not available on this device")
            stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount < 0) {
                initialStepCount = event.values[0].toInt()
            }

            currentStepCount = event.values[0].toInt() - initialStepCount
            updateNotification(currentStepCount)
            saveStepCount(currentStepCount)

            // Send step count to ContextCoordinator
            val healthData = JSONObject().apply {
                put("step_count", currentStepCount)
                put("timestamp", System.currentTimeMillis() / 1000)
            }
            // Uncomment this when ready to integrate with your health monitoring:
            // ContextCordinator.setHealth(healthData)

            Log.d(TAG, "Steps counted: $currentStepCount")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for step counter
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Step Counter Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for Step Counter Service"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(stepCount: Int) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Step Counter Active")
        .setContentText("Steps: $stepCount")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentIntent(PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        ))
        .build()

    private fun updateNotification(stepCount: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(stepCount))
    }

    private fun saveStepCount(count: Int) {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("step_count", count).apply()
    }

    companion object {
        private const val TAG = "StepCounterService"
    }
}