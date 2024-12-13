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

            if (geofence.requestId == "Meerkats") {
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered Growing Wilds Areas")
                        Toast.makeText(context, "Welcome to Growing Wilds Areas!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited Growing Wilds Areas")
                        Toast.makeText(context, "You have exited Growing Wilds Areas.", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            } else if (geofence.requestId == "Penguins") {
                // Handle Google Head Quarter geofence similarly
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered  Penguins | Sea Creatures Area")
                        Toast.makeText(context, "Welcome to Penguins Area!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited Penguins Area")
                        Toast.makeText(context, "You have exited Penguins Area.", Toast.LENGTH_LONG).show()
                    }
                }
            } else if (geofence.requestId == "Lions") {
                // Handle Google Head Quarter geofence similarly
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered Lions Area")
                        Toast.makeText(context, "Welcome to  Lions Area!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited Lions Area")
                        Toast.makeText(context, "You have exited Lions Area", Toast.LENGTH_LONG).show()
                    }
                }
            }else if (geofence.requestId == "GiantsTortoises") {
                // Handle Google Head Quarter geofence similarly
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered  Giants Tortoises Area")
                        Toast.makeText(context, "Welcome to Giants Tortoises Area!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited  Giants Tortoises Area")
                        Toast.makeText(context, "You have exited  Giants Tortoises Area.", Toast.LENGTH_LONG).show()
                    }
                }
            }else if (geofence.requestId == "Koalas") {
                // Handle Google Head Quarter geofence similarly
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered Koala Australia Native")
                        Toast.makeText(context, "Welcome to Koala Australia Nativer!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited Koala Australia Native Area")
                        Toast.makeText(context, "You have exited Koala Australia Native Area.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else if (geofence.requestId == "Elephants") {
                // Handle Google Head Quarter geofence similarly
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered Elephant Area")
                        Toast.makeText(context, "Welcome to Elephant Area!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited Elephant Area")
                        Toast.makeText(context, "You have exited  Elephant Area.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else if (geofence.requestId == "Orangutans") {
                // Handle Google Head Quarter geofence similarly
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered  Orangutans")
                        Toast.makeText(context, "Welcome to Google Head Quarter!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited  Orangutans")
                        Toast.makeText(context, "You have exited  Orangutans.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else if (geofence.requestId == "Entrance&Exit") {
                // Handle Google Head Quarter geofence similarly
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered  Entrance & Exit")
                        Toast.makeText(context, "Welcome to Google Head Quarter!", Toast.LENGTH_LONG).show()
                    }

                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        Log.d("GeofenceReceiver", "Exited  Entrance & Exit")
                        Toast.makeText(context, "You have exited  Google Head Quarter.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}
