package com.example.dcis2

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class TestDataRetrievalActivity : AppCompatActivity() {

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofencePendingIntent: PendingIntent
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var retrievedDataTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_retrival_test)

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofencePendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Initialize the TextView
        retrievedDataTextView = findViewById(R.id.retrievedDataTextView)

        // Access SharedPreferences
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Retrieve the data
        val retrievedData = retrieveSavedData()

        // Check if data exists
        if (retrievedData.isNotEmpty()) {
            retrievedDataTextView.text = "Retrieved Data:\n$retrievedData"
        } else {
            retrievedDataTextView.text = "No data found in SharedPreferences."
            Toast.makeText(this, "No data to display.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun retrieveSavedData(): String {
        // Retrieve all key-value pairs from SharedPreferences
        val allEntries = sharedPreferences.all
        return if (allEntries.isNotEmpty()) {
            allEntries.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        } else {
            "" // Return empty string if no data exists
        }
    }
    private fun createGeofence(enclosureId: String, latitude: Double, longitude: Double, radius: Float): Geofence {
        return Geofence.Builder()
            .setRequestId(enclosureId)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE) // Geofence never expires
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    private fun buildGeofencingRequest(geofences: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()
    }


}
