package com.example.dcis2

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.dcis2.ultility.LocationUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import org.dcis.ContextCordinator
import org.json.JSONObject

data class EnclosureLocation(val latitude: Double, val longitude: Double)

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val handler = Handler(Looper.getMainLooper())
    private val locationUpdateInterval: Long = 60000 // 1 minute
    private val geofenceMessages = mapOf(
        "Meerkats" to EnclosureLocation(-37.78472222, 144.95333333),
        "AmazonBirds" to EnclosureLocation(-37.78472222, 144.95333333),
        "Penguins" to EnclosureLocation(-37.78388889,  144.95222222),
        "Lions" to EnclosureLocation(-37.78333333,  144.95166667),
        "GiantsTortoises" to EnclosureLocation(-37.78333333 ,  144.952027778),
        "Koalas" to EnclosureLocation(-37.78444444,   144.95027778),
        "Elephants" to EnclosureLocation(- 37.78583333,   144.94972222),
        "Orangutans" to EnclosureLocation(-37.78527778,  144.9511111),
        "Entrance&Exit" to EnclosureLocation(- 37.78527778,  144.95305556)
    )

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] // Distance in meters
    }

    private fun fetchLocationAndSend(context: Context, enclosureTag: String, action: String) {
        LocationUtils.fetchLocation(context) { latitude, longitude ->
            val enclosureLocation = geofenceMessages[enclosureTag.removeSuffix("_enc")] as? EnclosureLocation
            val distance = enclosureLocation?.let {
                calculateDistance(latitude, longitude, it.latitude, it.longitude)
            } ?: 0f // Handle case where enclosure location is not found
            val locationJson = createLocationJson(enclosureTag, distance,latitude, longitude)
            Log.d("GeofenceReceiver", "Location JSON: $locationJson")
            ContextCordinator.setLocation(locationJson)
        }
    }

    private fun createLocationJson(enclosureTag: String, distance: Float, latitude: Double? = null, longitude: Double? = null, enclosureLatitude: Double? = null, enclosureLongitude: Double? = null): JSONObject {
        val locationJson = JSONObject()
        locationJson.put("enclosureTag", enclosureTag)
        locationJson.put("distance", distance) // Assuming "action" is meant to store distance
        latitude?.let { locationJson.put("latitude", it) }
        longitude?.let { locationJson.put("longitude", it) }
        enclosureLatitude?.let { locationJson.put("enclosureLatitude", it) }
        enclosureLongitude?.let { locationJson.put("enclosureLongitude", it) }
        return locationJson
    }

    private fun startPeriodicLocationUpdates(context: Context, enclosureTag: String) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Fetch the current location and send it to the backend
                LocationUtils.fetchLocation(context) { latitude, longitude ->
                    val enclosureLocation = geofenceMessages[enclosureTag.removeSuffix("_enc")] as? EnclosureLocation
                    val distance = enclosureLocation?.let {
                        calculateDistance(latitude, longitude, it.latitude, it.longitude)
                    } ?: 0f // Handle case where enclosure location is not found
                    val locationJson = createLocationJson(enclosureTag, distance, latitude, longitude, enclosureLocation?.latitude, enclosureLocation?.longitude)
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
