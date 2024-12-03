package com.example.dcis2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                if (geofencingEvent != null) {
                    Log.e("Geofencing", "Error in geofencing: ${geofencingEvent.hasError()}")
                }
                return
            }
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            if (geofencingEvent != null) {
                Log.d("Geofencing", "Entered geofence: ${geofencingEvent.triggeringGeofences}")
            }
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            if (geofencingEvent != null) {
                Log.d("Geofencing", "Exited geofence: ${geofencingEvent.triggeringGeofences}")
            }
        }
    }
}
