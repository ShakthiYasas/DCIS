package com.example.dcis2

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.dcis2.ultility.LocationUtils
import com.example.dcis2.utility.HealthSensorUtils
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class HealthSensorActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private lateinit var heartRateTextView: TextView
    private lateinit var stepCountTextView: TextView

ivate val LOCATION_PERMISSION_REQUEST_CODE = 1000
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
        if (LocationUtils.checkAndRequestLocationPermission(this)) {
            LocationUtils.fetchLocation(this) { latitude, longitude ->
                Toast.makeText(this, "Location: $latitude, $longitude", Toast.LENGTH_LONG).show()
            }
        }
        geofencingClient = LocationServices.getGeofencingClient(this)

//        // Set up Oakland Zoo geofence
        val geofenceList = listOf(
            createGeofence("oakland_zoo_enc", 37.4220936, -122.083922, 60f) // Oakland Zoo geofence
        )
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()

//        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
//            .addOnSuccessListener {
//                Log.d("Geofencing", "NEW Geofences added successfully")
//            }
//            .addOnFailureListener { exception ->
//                if (exception is ApiException) {
//                    // Handle the ApiException, e.g., check the status code
//                    when (exception.statusCode) {
//                        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> {
//                            // Location services might be disabled
//                        }
//                        // ... handle other status codes
//                    }            }
//
//            }
//        addGeofences(geofenceList)

        geofenceList2 = ArrayList()
        // Check permissions
        if (hasLocationPermissions()) {
            setupGeofences()
        } else {
            requestLocationPermissions()
        }

        // Example Geofence37.773628, -122.154722
        geofenceList2.add(createGeofence("Home", 37.7749, -122.4194, 150f))
        geofenceList2.add(createGeofence("oakland_zoo_enc", 37.773628, -122.154722, 60f))
        geofenceList2.add(createGeofence("Google_Head_quarter", 37.4220936, -122.083922, 60f))

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

    @SuppressLint("MissingPermission")
    private fun addGeofences(geofences: List<Geofence>) {
        if (checkPermissions()) {
                geofencingClient.addGeofences(buildGeofencingRequest(geofences), geofencePendingIntent)
                    .addOnSuccessListener {
                        Log.d("Geofencing", "Geofences added successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Geofencing", "Failed to add geofences", e)
                    }
        } else {
            requestPermissions()
        }
    }
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun buildGeofencingRequest(geofences: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()
    }

    private fun createGeofence(id: String, lat: Double, lng: Double, radius: Float): Geofence {
        return Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(lat, lng, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList2)
            .build()
    }

    private fun setupGeofences() {
        if (!hasLocationPermissions()) {
            Log.e("HealthSensorActivity", "Location permissions not granted")
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)
            .addOnSuccessListener {
                Log.i("HealthSensorActivity", "Geofences added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("HealthSensorActivity", "Failed to add geofences: ${e.message}")
            }
    }
    private fun hasLocationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else {
            TODO("VERSION.SDK_INT < Q")
        }
    }

    private fun requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }




}
