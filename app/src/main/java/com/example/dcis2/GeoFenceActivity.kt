package com.example.dcis2

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class GeoFenceActivity: FragmentActivity(), OnMapReadyCallback{

    private var mMap: GoogleMap? = null
    private var geofencingClient: GeofencingClient? = null
    private lateinit var geofenceHelper: GeofenceHelper

    companion object {
        private const val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
        private const val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_geofence)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)
        Handler(Looper.getMainLooper()).postDelayed({
            addGeofences() // Call your geofence addition logic here
        }, 2000)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val melbourneZoo = LatLng(-37.78472222, 144.95333333)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourneZoo, 20f))

        enableUserLocation()
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
               Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap?.isMyLocationEnabled = true
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_ACCESS_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_ACCESS_REQUEST_CODE
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                mMap?.isMyLocationEnabled = true
            } else {
                //We do not have the permission..
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofence...", Toast.LENGTH_SHORT).show()
            } else {
                //We do not have the permission..
                Toast.makeText(
                    this,
                    "Background location access is necessary for geofence to trigger...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createGeofence(id: String, lat: Double, lng: Double, radius: Float): Geofence {
        return Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(lat, lng, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addGeofences(){
        geofencingClient = LocationServices.getGeofencingClient(this)
        val geofenceList: MutableList<Geofence> = mutableListOf()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Geofencing", "CHECK PERMISSION NOT GRANTED")

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                1000
            )
            return
        }

        // Add Geofence objects to the list
        geofenceList.add(createGeofence("Meerkats", -37.78472222, 144.95333333, 10f))
        geofenceList.add(createGeofence("AmazonBirds", -37.78472222, 144.95333333, 10f))
        geofenceList.add(createGeofence("Penguins", -37.78388889, 144.95222222, 10f))
        geofenceList.add(createGeofence("Lions", - 37.78333333, 144.95166667, 10f))
        geofenceList.add(createGeofence("GiantTortoises", - 37.78333333, 144.95027778, 10f))
        geofenceList.add(createGeofence("Koalas", - 37.78444444, 144.95027778, 10f))
        geofenceList.add(createGeofence("Elephants", - 37.78583333, 144.94972222, 10f))
        geofenceList.add(createGeofence("Orangutans", -37.78527778, 144.9511111, 10f))
        geofenceList.add(createGeofence("Entrance&Exit", - 37.78527778, 144.95305556, 10f))
        geofenceList.add(createGeofence("Node1", - 37.7972222, 144.95277778, 10f))
        geofenceList.add(createGeofence("Node2", - 37.78444444, 144.95222222, 10f))
        geofenceList.add(createGeofence("Node3", - 37.78416667, 144.95222222, 10f))
        geofenceList.add(createGeofence("Node4", - 37.78416667, 144.95166667, 10f))
        geofenceList.add(createGeofence("Node5", - 37.78361111, 144.95138889, 10f))

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()

        geofencingClient!!.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                // Geo fences added successfully
                Log.d("Geofencing", "Geo fences added successfully")
            }
            addOnFailureListener {
                Log.d("Geofencing", "Geo fences failed to add ")
            }
        }

    }
}