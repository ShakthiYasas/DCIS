package com.example.dcis2

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import android.widget.Toast
import android.content.Intent
import android.widget.Button
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import com.example.dcis2.ultility.PreferencesUtils
import com.example.dcis2.ultility.SpinnerValidationUtils
import com.example.dcis2.utility.HealthServicesUtils


class DisplayQRDataActivity : AppCompatActivity() {

    private lateinit var btnSwitchToAnimalPreference: Button
    private lateinit var sharedPreferences: SharedPreferences
    val spinnerRequiredPairs = mutableListOf<Pair<Spinner, TextView>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_result)
        sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        btnSwitchToAnimalPreference = findViewById(R.id.bSuttonGoButton)


        // Retrieve the JSON string passed from MainActivity
        val qrData = intent.getStringExtra("qr_data") ?: "{}"

        // Parse the JSON data
        val jsonObject = JSONObject(qrData)
        val numberOfAdults = jsonObject.optInt("Number of Adults", 0)
        val numberOfChildren = jsonObject.optInt("Number of Children", 0)

        val container = findViewById<LinearLayout>(R.id.container)
        // Parse the JSON string to extract data
        val ageRanges = arrayOf("None","18-25", "25-45", "45-75", "75-100")
        val childrenAgeRanges = arrayOf("None","0-5", "5-10", "10-15", "15-18")

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
            // Create a horizontal layout for spinner and checkbox
            val adultLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            // Create a Spinner for selecting age range
            val adultSpinner = Spinner(this)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageRanges)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adultSpinner.adapter = adapter

            val requiredTextView = TextView(this).apply {
                text = "Required"
                setTextColor(Color.RED)
                textSize = 14f
                visibility = TextView.GONE // Initially hidden
                setPadding(16, 0, 0, 0)
            }

            spinnerRequiredPairs.add(adultSpinner to requiredTextView)

            adultLayout.addView(adultSpinner)
            adultLayout.addView(requiredTextView)
            container.addView(adultLayout)

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
            // Create a horizontal layout for spinner and checkbox
            val childLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            // Create a Spinner for selecting age range
            val childSpinner = Spinner(this)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, childrenAgeRanges)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            childSpinner.adapter = adapter

            val requiredTextView = TextView(this).apply {
                text = "Required"
                setTextColor(Color.RED)
                textSize = 14f
                visibility = TextView.GONE // Initially hidden
                setPadding(16, 0, 0, 0)
            }

            spinnerRequiredPairs.add(childSpinner to requiredTextView)

            childLayout.addView(childSpinner)
            childLayout.addView(requiredTextView)
            container.addView(childLayout)

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
            if (SpinnerValidationUtils.validateSpinnerSelections(spinnerRequiredPairs)) {
                // Proceed to the next screen
                val intent = Intent(this@DisplayQRDataActivity, AnimalPreferenceActivity::class.java)
                startActivity(intent)

                // Request Health Services Permission (triggered after switch)
                HealthServicesUtils.requestHealthServices(this)
            } else {
                Toast.makeText(this@DisplayQRDataActivity, "Please complete all required fields.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        HealthServicesUtils.handleHealthPermissionResult(requestCode, grantResults, this)
    }

    private fun saveAgeRangeToPreferences(key: String, ageRange: String) {
        PreferencesUtils.saveToPreferences(this, key, ageRange)
        Toast.makeText(this, "Saved age range for $key: $ageRange", Toast.LENGTH_SHORT).show()
    }
}