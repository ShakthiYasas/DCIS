package com.example.dcis2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.dcis2.utility.BluetoothUtils
import org.dcis.ContextCordinator
import org.json.JSONObject

import java.util.UUID

class TestDataRetrievalActivity : AppCompatActivity() {
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Replace with your UUID

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private lateinit var showJsonButton: Button
    private lateinit var retrievedDataTextView: TextView
    private lateinit var navigateToHealthSensorButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_retrival_test)
        // Initialize the TextView
        retrievedDataTextView = findViewById(R.id.retrievedDataTextView)
        showJsonButton = findViewById(R.id.showJsonButton)
        retrievedDataTextView = findViewById(R.id.retrievedDataTextView)
        navigateToHealthSensorButton = findViewById(R.id.navigateToHealthSensorButton)

        val testIntegrationButton = findViewById<Button>(R.id.testIntegrationButton)
        testIntegrationButton.setOnClickListener {
            testIntegration()
        }
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
        val setupEdgeButton = findViewById<Button>(R.id.setupEdgeButton)
        setupEdgeButton.setOnClickListener {
            setupEdge()
        }

        // Example button to send location data
        val sendLocationButton = findViewById<Button>(R.id.sendLocationButton)
        sendLocationButton.setOnClickListener {
            sendLocationData()
        }

        // Example button to send health data
        val sendHealthButton = findViewById<Button>(R.id.sendHealthButton)
        sendHealthButton.setOnClickListener {
            sendHealthData()
        }

        showJsonButton.setOnClickListener {
            val sharedPreferencesJson = getSharedPreferencesAsJson()
            retrievedDataTextView.text = sharedPreferencesJson.toString()
        }

        BluetoothUtils.requestBluetoothPermissions(this)
        BluetoothUtils.enableBluetooth(this)

        // Button to navigate to HealthSensorActivity
        navigateToHealthSensorButton.setOnClickListener {
            val intent = Intent(this, HealthSensorActivity::class.java)
            startActivity(intent)
        }

        
        // Determine the audience based on adult/child count
        sendProfileToContextCoordinator()

        val verifyBluetoothButton = findViewById<Button>(R.id.verifyBluetoothButton)
        verifyBluetoothButton.setOnClickListener {
//            verifyBluetoothConnection()
        }

    }


    private fun testIntegration() {
        val response = ContextCordinator.health()
        Log.d("TestIntegration", "Response: $response")
    }
    private fun setupEdge() {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val audience = sharedPreferences.getString("audience", "adult") ?: "adult"
        val sessionId = getSessionId()
        val healthPermission = hasHealthPermissions()

        val config = JSONObject().apply {
            put("audience", audience)
            put("session_id", sessionId)
            put("health_permission", healthPermission)
        }

        val response = ContextCordinator.setupEdge(config)
        Log.d("SetupEdge", "Response: $response")
    }
    private fun sendLocationData() {
        val locationData = JSONObject().apply {
            put("tag", "elephant_enc")
            put("timestamp", System.currentTimeMillis() / 1000)
            put("distance", 10.0)
            put("latitude", 23.546547)
            put("longitude", -45.902735)
        }

        ContextCordinator.setLocation(locationData)
        Log.d("SendLocationData", "Location data sent: $locationData")
    }
    private fun sendHealthData() {
        val healthData = JSONObject().apply {
            put("body_temperature", 37.8)
            put("heart_rate", 88)
            put("timestamp", System.currentTimeMillis() / 1000)
        }

        ContextCordinator.setHealth(healthData)
        Log.d("SendHealthData", "Health data sent: $healthData")
    }

    private fun verifyBluetoothSender(hashKey: String) {
        val response = ContextCordinator.verifyBluetoothSender(hashKey)
        Log.d("VerifyBluetoothSender", "Response: $response")

        // Handle the response
        val status = response.getInt("status")
        if (status == 200) {
            val session = response.getString("session")
            Toast.makeText(this, "BLE authorized. Session: $session", Toast.LENGTH_SHORT).show()
        } else {
            val error = response.getString("error")
            Toast.makeText(this, "BLE verification failed: $error", Toast.LENGTH_SHORT).show()
        }
    }


    private fun determineAudienceByCount(): String {
        val allEntries = sharedPreferences.all
        var adultCount = 0
        var childCount = 0
        var seniorCount = 0
        var youngAdultCount = 0
        var infantCount = 0
        var olderChildCount = 0

        for ((key, value) in allEntries) {
            if (key.startsWith("adult_")) {
                adultCount++
                when (value) {
                    "65-75", "75-100" -> seniorCount++
                    "25-45", "45-65" -> youngAdultCount++
                }
            } else if (key.startsWith("child_")) {
                childCount++
                when (value) {
                    "0-5" -> infantCount++
                    "5-10", "10-15", "15-18" -> olderChildCount++
                }
            }
        }

        return when {
            adultCount > childCount -> {
                if (seniorCount > youngAdultCount) {
                    "senior"
                } else {
                    "adult"
                }
            }
            childCount > adultCount -> {
                if (infantCount > olderChildCount) {
                    "infant"
                } else {
                    "children"
                }
            }
            else -> "children" // Tie-breaker: children
        }
    }

    private fun sendProfileToContextCoordinator() {

        val audience = determineAudienceByCount()
        val sessionId = getSessionId()
        val healthPermission = hasHealthPermissions()
        Toast.makeText(this, "Audience: $audience", Toast.LENGTH_LONG).show()
        Log.d("TestDataRetrieval", "Audience: $audience")
        val contextData = JSONObject()
        contextData.put("audience", audience)
        contextData.put("session_id", sessionId)
        contextData.put("health_permission", healthPermission)

//        ContextCordinator.setupEdge(contextData)
        Log.d("ProfileSelection", "Context data sent: $contextData")
    }

    private fun hasHealthPermissions(): Boolean {
        val hasBodySensorsPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED

        val hasActivityRecognitionPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        return hasBodySensorsPermission && hasActivityRecognitionPermission
    }


    private fun getSessionId(): String {
        val deviceName = getDeviceName()
        return deviceName.replace(Regex("[\\s\\p{Punct}]"), "") // Remove whitespace and punctuation
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else {
            "$manufacturer $model"
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
