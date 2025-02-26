package com.example.dcis2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dcis2.ultility.LocationUtils
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import org.dcis.ContextCordinator
import org.json.JSONObject
import android.Manifest
import android.speech.tts.TextToSpeech
import java.util.Locale

data class EnclosureLocation(val latitude: Double, val longitude: Double)

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val handler = Handler(Looper.getMainLooper())
    private val locationUpdateInterval: Long = 30000 // 30 seconds
    private var currentEnclosureTag: String? = null // Track the current enclosure tag
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val geofenceMessages = mapOf(
        "meerkat_enc" to EnclosureLocation(-37.78472222, 144.95333333),
        "bird_enc" to EnclosureLocation(-37.78472222, 144.95333333),
        "penguin_enc" to EnclosureLocation(-37.78388889,  144.95222222),
        "lion_enc" to EnclosureLocation(-37.78333333,  144.95166667),
        "tortoise_enc" to EnclosureLocation(-37.78333333 ,  144.952027778),
        "koala_enc" to EnclosureLocation(-37.78444444,   144.95027778),
        "elephant_enc" to EnclosureLocation(- 37.78583333,   144.94972222),
        "orangutan_enc" to EnclosureLocation(-37.78527778,  144.9511111),
        "entexit" to EnclosureLocation(- 37.78527778,  144.95305556),
        "Node1" to EnclosureLocation(- 37.7972222,144.95277778),
        "Node2" to EnclosureLocation(- 37.78444444,144.95277778),
        "Node3" to EnclosureLocation(- 37.78416667,144.95222222),
        "Node4" to EnclosureLocation(- 37.78416667,144.95222222),
        "Node5" to EnclosureLocation(- 37.7836111,144.95138889),
        "Node6" to EnclosureLocation(-37.7833333 ,144.9501111),
        "Node7" to EnclosureLocation(- 37.78277778,144.95138889),
        "Node8" to EnclosureLocation(-37.78388889 ,144.94972222),
        "Node9" to EnclosureLocation(-37.78416667 ,144.95027778),
        "Node10" to EnclosureLocation(-37.78583333 ,144.95000000),
        "Node11" to EnclosureLocation(-37.78527178 ,144.95083333),
        )

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] // Distance in meters
    }

    private fun createLocationJson(enclosureTag: String, distance: Float, latitude: Double? = null, longitude: Double? = null): JSONObject {
        val locationJson = JSONObject()
        locationJson.put("enclosureTag", enclosureTag)
        locationJson.put("distance", distance.toDouble())
        locationJson.put("longitude", longitude?:0)
        locationJson.put("latitude", latitude?: 0)

        return locationJson
    }
    private fun fetchLocationAndSend(context: Context, enclosureTag: String, action: String) {
        currentEnclosureTag = enclosureTag // Store the current enclosure tag
        LocationUtils.fetchLocation(context) { latitude, longitude ->
            val enclosureLocation = geofenceMessages[enclosureTag.removeSuffix("_enc")]
            val distance = enclosureLocation?.let {
                calculateDistance(latitude, longitude, it.latitude, it.longitude)
            } ?: 0f // Handle case where enclosure location is not found
            val locationJson = createLocationJson(enclosureTag, distance,latitude, longitude)
            Log.d("GeofenceReceiver", "Location JSON: $locationJson")
            ContextCordinator.setLocation(locationJson)
            showNotification(context, enclosureTag, distance)
        }
    }

    private fun initializeTTS(context: Context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                } else {
                    Log.i("TTS", "TTS initialized successfully")
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }
    private fun startPeriodicLocationUpdates(context: Context, enclosureTag: String) {
        currentEnclosureTag = enclosureTag // Store the current enclosure tag
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Fetch the current location and send it to the backend
                LocationUtils.fetchLocation(context) { latitude, longitude ->
                    val enclosureLocation = geofenceMessages[enclosureTag.removeSuffix("_enc")]
                    val distance = enclosureLocation?.let {
                        calculateDistance(latitude, longitude, it.latitude, it.longitude)
                    } ?: 0f // Handle case where enclosure location is not found
                    val locationJson = createLocationJson(enclosureTag, distance, latitude, longitude)
                    Log.d("GeofenceReceiver", "Periodic Location JSON: $locationJson")
                    ContextCordinator.setLocation(locationJson)
                    // Create and show notification
                    showNotification(context, enclosureTag, distance)
                }
                handler.postDelayed(this, locationUpdateInterval)
            }
        }, locationUpdateInterval)
    }

    private fun stopPeriodicLocationUpdates() {
        handler.removeCallbacksAndMessages(null)
        currentEnclosureTag = null // Clear the current enclosure tag
//        ContextCordinator.setLocation(locationJson)

    }

    private fun showNotification(context: Context, enclosureTag: String, distance: Float) {
        val channelId = "geofence_channel"
        val notificationId = System.currentTimeMillis().toInt() // Unique ID for each notification

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your notification icon
            .setContentTitle("Geofence Triggered")
            .setContentText("You are near $enclosureTag. Distance: $distance meter")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismiss notificat=ion when tapped

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
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


        val notificationText = "You are near $enclosureTag. Distance: $distance"
        notificationBuilder.setContentText(notificationText)

        // Speak the notification text
        speakText(notificationText)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        Log.d("Geofencing", "Hello from Broadcast Receiver | Geo fences received")
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            val errorCode = geofencingEvent?.errorCode
            Log.e("GeofenceReceiver", "Error in geofencing event. Error code: $errorCode. Intent extras: ")
            return
        }
        if (!isTtsInitialized) {
            initializeTTS(context)
            isTtsInitialized = true
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences ?: emptyList()

        for (geofence in triggeringGeofences) {
            Log.d("GeofenceReceiver", "Triggered geofence: ${geofence.requestId}")
            for (geofence in triggeringGeofences) {
                val enclosureTag = geofence.requestId
                val areaMessage = geofenceMessages[geofence.requestId] ?: "Unknown Area"

                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        Log.d("GeofenceReceiver", "Entered $areaMessage")
                        fetchLocationAndSend(context, enclosureTag, "enter")
//                      startPeriodicLocationUpdates(context, enclosureTag)
                        Toast.makeText(context, "Welcome to $areaMessage!", Toast.LENGTH_LONG).show()
                    }
                    Geofence.GEOFENCE_TRANSITION_DWELL ->{
                        Toast.makeText(context, "Continue to stay at $areaMessage!", Toast.LENGTH_LONG).show()
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

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
//    fun onDestroy() {
//        if (tts != null) {
//            tts?.stop()
//            tts?.shutdown()
//        }
//        super.onDestroy() }
//
//
}
