package com.example.dcis2

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.json.JSONObject
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.Intent
import android.widget.Button
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.AdapterView

class DisplayQRDataActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private lateinit var btnSwitchToAnimalPreference: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_result)
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, fetch location
            fetchLocation()
        }
        // Retrieve the JSON string passed from MainActivity
        val qrData = intent.getStringExtra("qr_data") ?: "{}"

        // Parse the JSON data
        val jsonObject = JSONObject(qrData)
        val numberOfAdults = jsonObject.optInt("Number of Adults", 0)
        val numberOfChildren = jsonObject.optInt("Number of Children", 0)

        val container = findViewById<LinearLayout>(R.id.container)
        // Parse the JSON string to extract data
        val ageRanges = arrayOf("18-25", "25-45", "45-75", "75-100")
        val childrenAgeRanges = arrayOf("0-5", "5-10", "10-15", "15-18")

        val dataList = mutableListOf<String>()
        jsonObject.keys().forEach { key ->
            val value = jsonObject.get(key)
            dataList.add("$key: $value")
        }

        // Dynamically create components for each adult
        for (i in 1..numberOfAdults) {
            // Create a TextView for the adult label
            val adultLabel = TextView(this).apply {
                text = "Select age range for Adult $i:"
                textSize = 16f
                setPadding(0, 16, 0, 8)
            }
            container.addView(adultLabel)

            // Create a Spinner for selecting age range
            val adultSpinner = Spinner(this)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageRanges)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adultSpinner.adapter = adapter
            container.addView(adultSpinner)

            // Save selected age range when changed
            adultSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    val selectedAgeRange = ageRanges[position]
                    saveAgeRangeToPreferences("adult_$i", selectedAgeRange)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }
        }

        // Dynamically create components for each child
        for (i in 1..numberOfChildren) {
            // Create a TextView for the child label
            val childLabel = TextView(this).apply {
                text = "Select age range for Child $i:"
                textSize = 16f
                setPadding(0, 16, 0, 8)
            }
            container.addView(childLabel)

            // Create a Spinner for selecting age range
            val childSpinner = Spinner(this)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, childrenAgeRanges)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            childSpinner.adapter = adapter
            container.addView(childSpinner)

            // Save selected age range when changed
            childSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    val selectedAgeRange = childrenAgeRanges[position]
                    saveAgeRangeToPreferences("child_$i", selectedAgeRange)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }
        }
        // Initialize the button and set up the click listener
        val btnSwitchToAnimalPreference = findViewById<Button>(R.id.bSuttonGoButton)
        btnSwitchToAnimalPreference.setOnClickListener {
            // Launch AnimalPreferenceActivity
            val intent = Intent(this, AnimalPreferenceActivity::class.java)
            startActivity(intent)

            // Request Health Services Permission (triggered after switch)
        }
    }

    private fun saveAgeRangeToPreferences(key: String, ageRange: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, ageRange)
        editor.apply() // Save asynchronously
        Toast.makeText(this, "Saved age range for $key: $ageRange", Toast.LENGTH_SHORT).show()
    }

    private fun fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Use the location data here
                    Toast.makeText(
                        this,
                        "Latitude: ${location.latitude}, Longitude: ${location.longitude}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Unable to retrieve location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, fetch location
                fetchLocation()
            } else {
                // Permission denied
                Toast.makeText(
                    this,
                    "Location permission is required for the application to function correctly.",
                    Toast.LENGTH_LONG
                ).show()
                btnSwitchToAnimalPreference.isEnabled = false
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}