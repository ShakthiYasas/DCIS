package com.example.dcis2

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TestDataRetrievalActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var retrievedDataTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_retrival_test)

        // Initialize the TextView
        retrievedDataTextView = findViewById(R.id.retrievedDataTextView)

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
}
