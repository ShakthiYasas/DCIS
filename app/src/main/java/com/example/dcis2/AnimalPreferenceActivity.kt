package com.example.dcis2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AnimalPreferenceActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnAll: Button
    private lateinit var gridView: GridView
    private lateinit var btnSavePreferences: Button
    private val categories = listOf(
        "Growing Wild", "Birds", "Sea Creatures", "Predators",
        "Reptiles", "Australian Natives", "Rainforest", "Apes and Monkeys"
    )
    private val selectedCategories = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_preference)

        // Initialize components
        sharedPreferences = getSharedPreferences("AnimalPreferences", MODE_PRIVATE)
        btnAll = findViewById(R.id.btnAll)
        gridView = findViewById(R.id.gridViewAnimalCategories)
        btnSavePreferences = findViewById(R.id.btnSavePreferences)

        // Populate GridView with category buttons
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            categories
        )
        gridView.adapter = adapter
        gridView.choiceMode = GridView.CHOICE_MODE_MULTIPLE

        // "All" Button Logic
        btnAll.setOnClickListener {
            if (selectedCategories.size != categories.size) {
                // Select all categories
                selectedCategories.clear()
                selectedCategories.addAll(categories)
                for (i in categories.indices) gridView.setItemChecked(i, true)
            } else {
                // Deselect all categories
                selectedCategories.clear()
                for (i in categories.indices) gridView.setItemChecked(i, false)
            }
            updateAllButtonState()
        }

        // Handle category selection
        gridView.setOnItemClickListener { _, _, position, _ ->
            val category = categories[position]
            if (selectedCategories.contains(category)) {
                selectedCategories.remove(category)
            } else {
                selectedCategories.add(category)
            }
            updateAllButtonState()
        }

        // Save Preferences Button
        btnSavePreferences.setOnClickListener {
            savePreferences()
            Toast.makeText(this, "Preferences saved!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@AnimalPreferenceActivity, TestDataRetrievalActivity::class.java)

            startActivity(intent)        }
    }

    // Update button states
    private fun updateAllButtonState() {
        if (selectedCategories.size == categories.size) {
            btnAll.isEnabled = true
            btnAll.text = "All Selected"
        } else if (selectedCategories.isNotEmpty()) {
            btnAll.isEnabled = false
        } else {
            btnAll.isEnabled = true
            btnAll.text = "Select All"
        }
    }

    // Save preferences to SharedPreferences
    private fun savePreferences() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("SelectedCategories", selectedCategories)
        editor.apply()
    }
}
