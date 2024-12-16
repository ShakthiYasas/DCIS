package com.example.dcis2

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.dcis2.ultility.LocationUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import org.dcis.ContextCordinator
import org.json.JSONObject

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val handler = Handler(Looper.getMainLooper())
    private val locationUpdateInterval: Long = 60000 // 1 minute
    private val geofenceMessages = mapOf(
        "Beermats" to "Beermats Area",
        "Penguins" to "Penguins Area",
        "Lions" to "Lions Area",
        "GiantsTortoises" to "Giants Tortoises Area",
        "Koalas" to "Koala Australia Native Area",
        "Elephants" to "Elephant Area",
        "Orangutans" to "Orangutans Area",
        "Entrance&Exit" to "Entrance & Exit Area"
    )

    private fun fetchLocationAndSend(context: Context, enclosureTag: String, action: String) {
        LocationUtils.fetchLocation(context) { latitude, longitude ->
            val locationJson = createLocationJson(enclosureTag, action, latitude, longitude)
            Log.d("GeofenceReceiver", "Location JSON: $locationJson")
            ContextCordinator.setLocation(locationJson)
        }
    }

    private fun createLocationJson(enclosureTag: String, action: String, latitude: Double? = null, longitude: Double? = null): JSONObject {
        val locationJson = JSONObject()
        locationJson.put("enclosureTag", enclosureTag)
        locationJson.put("action", action)
        latitude?.let { locationJson.put("latitude", it) }
        longitude?.let { locationJson.put("longitude", it) }
        return locationJson
    }

    private fun startPeriodicLocationUpdates(context: Context, enclosureTag: String) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Fetch the current location and send it to the backend
                LocationUtils.fetchLocation(context as Activity) { latitude, longitude ->
                    val locationJson = createLocationJson(enclosureTag, "update", latitude, longitude)
                    Log.d("GeofenceReceiver", "Periodic Location JSON: $locationJson")
                    ContextCordinator.setLocation(locationJson)
                }
                handler.postDelayed(this, locationUpdateInterval)
            }
        }, locationUpdateInterval)
    }

    private fun stopPeriodicLocationUpdates() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        Log.d("Geofencing", "Hello from Broadcast Receiver | Geo fences received")
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            val errorCode = geofencingEvent?.errorCode
            Log.e("GeofenceReceiver", "Error in geofencing event. Error code: $errorCode. Intent extras: ")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences ?: emptyList()

        for (geofence in triggeringGeofences) {
            Log.d("GeofenceReceiver", "Triggered geofence: ${geofence.requestId}")

            for (geofence in triggeringGeofences) {
                val enclosureTag = "${geofence.requestId}_enc"
                val areaMessage = geofenceMessages[geofence.requestId] ?: "Unknown Area"

                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered $areaMessage")
                        fetchLocationAndSend(context, enclosureTag, "enter")
                        Toast.makeText(context, "Welcome to $areaMessage!", Toast.LENGTH_LONG).show()
                        startPeriodicLocationUpdates(context, enclosureTag)
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited $areaMessage")
                        fetchLocationAndSend(context, enclosureTag, "exit")
                        Toast.makeText(context, "You have exited $areaMessage.", Toast.LENGTH_LONG).show()
                        stopPeriodicLocationUpdates()
                    }
                }
            }

        }

    }

}
