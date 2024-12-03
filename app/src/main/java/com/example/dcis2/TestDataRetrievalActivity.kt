package com.example.dcis2

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dcis2.utility.BluetoothUtils
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.util.UUID

class TestDataRetrievalActivity : AppCompatActivity() {
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Replace with your UUID

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofencePendingIntent: PendingIntent
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var sendJsonButton: Button

    private lateinit var headerTextView: TextView
    private lateinit var showJsonButton: Button
    private lateinit var retrievedDataTextView: TextView
    private lateinit var navigateToHealthSensorButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_retrival_test)

//        geofencingClient = LocationServices.getGeofencingClient(this)
//        geofencePendingIntent = PendingIntent.getBroadcast(
//            this,
//            0,
//            Intent(this, GeofenceBroadcastReceiver::class.java),
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
        // Initialize the TextView
        retrievedDataTextView = findViewById(R.id.retrievedDataTextView)
        showJsonButton = findViewById(R.id.showJsonButton)
        retrievedDataTextView = findViewById(R.id.retrievedDataTextView)
        navigateToHealthSensorButton = findViewById(R.id.navigateToHealthSensorButton)

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

        BluetoothUtils.requestBluetoothPermissions(this)
        BluetoothUtils.enableBluetooth(this)
        // Button to show JSON data
        showJsonButton.setOnClickListener {
            // Simulating JSON data retrieval
            val sharedPreferencesJson = getSharedPreferencesAsJson()

            // Display JSON data in the TextView
            retrievedDataTextView.text = sharedPreferencesJson.toString()
        }
        // Button to navigate to HealthSensorActivity
        navigateToHealthSensorButton.setOnClickListener {
            val intent = Intent(this, HealthSensorActivity::class.java)
            startActivity(intent)
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
    private fun getSharedPreferencesAsJson(): JSONObject {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        val jsonObject = JSONObject()

        for ((key, value) in allEntries) {
            jsonObject.put(key, value)
        }

        return jsonObject
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        BluetoothUtils.handleBluetoothPermissionResult(requestCode, grantResults, this)
    }

    @SuppressLint("MissingPermission")
    private fun sendJsonToPairedDevice() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        // Get a list of paired devices
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        if (pairedDevices.isNullOrEmpty()) {
            Toast.makeText(this, "No paired devices found", Toast.LENGTH_SHORT).show()
            return
        }

        // Use the first paired device (or allow the user to select one)
        val device = pairedDevices.first()
        Toast.makeText(this, "Connecting to ${device.name}", Toast.LENGTH_SHORT).show()

        // Connect to the device and send JSON data
        val socket: BluetoothSocket? = BluetoothUtils.connectToDevice(device, uuid,this)
        socket?.let {
            val jsonData = "{\"message\":\"Hello from TestDataRetrievalActivity!\"}" // Example JSON data
            // Convert SharedPreferences to JSON
            val sharedPreferencesJson = getSharedPreferencesAsJson()
            BluetoothUtils.sendJsonData(it, sharedPreferencesJson)

            it.close() // Close the socket after sending
            Toast.makeText(this, "Data sent successfully to ${device.name}", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "Failed to connect to ${device.name}", Toast.LENGTH_SHORT).show()
        }
    }


}
