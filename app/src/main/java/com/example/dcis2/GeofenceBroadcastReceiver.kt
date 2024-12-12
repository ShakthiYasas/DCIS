package com.example.dcis2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import org.dcis.ContextCordinator
import org.json.JSONObject

class GeofenceBroadcastReceiver : BroadcastReceiver() {
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

            if (geofence.requestId == "oakland_zoo_enc") {
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered Oakland Zoo")
                        Toast.makeText(context, "Welcome to Oakland Zoo!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited Oakland Zoo")
                        Toast.makeText(context, "You have exited Oakland Zoo.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            if (geofence.requestId == "Google_Head_quarter") {
                // Handle Google Head Quarter geofence similarly
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered  Google Head Quarter")
                        Toast.makeText(context, "Welcome to Google Head Quarter!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited  Google Head Quarter")
                        Toast.makeText(context, "You have exited  Google Head Quarter.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}
