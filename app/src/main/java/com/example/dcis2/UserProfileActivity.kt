package com.example.dcis2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.dcis2.ultility.PreferencesUtils
import org.json.JSONObject

class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val familyButton: Button = findViewById(R.id.familyButton)
        val individualButton: Button = findViewById(R.id.individualButton)
        val groupButton: Button = findViewById(R.id.groupButton)
        val customButton: Button = findViewById(R.id.customButton)

        familyButton.setOnClickListener {
            saveFamilyProfile()
            navigateToNextActivity()
        }

        individualButton.setOnClickListener {
            saveIndividualProfile()
            navigateToNextActivity()
        }

        groupButton.setOnClickListener {
            saveGroupProfile()
            navigateToNextActivity()
        }

        customButton.setOnClickListener {
            navigateToScanQRActivity()
        }
    }

    private fun saveFamilyProfile() {
        PreferencesUtils.saveToPreferences(this, "Number of Adults", "2")
        PreferencesUtils.saveToPreferences(this, "Number of children", "2")
        PreferencesUtils.saveToPreferences(this, "adult_1", "25-45")
        PreferencesUtils.saveToPreferences(this, "adult_2", "25-45")
        PreferencesUtils.saveToPreferences(this, "children_1", "15-20")
        PreferencesUtils.saveToPreferences(this, "children_2", "15-20")
    }

    private fun saveIndividualProfile() {
        val profileData = JSONObject()
        PreferencesUtils.saveToPreferences(this, "Number of Adults", "1")
        PreferencesUtils.saveToPreferences(this, "Number of children", "0")
        profileData.put("adult_1", "18-25")
        PreferencesUtils.saveToPreferences(this, "adult_1", "25-45")
    }

    private fun saveGroupProfile() {
        for (i in 1..10) {
            PreferencesUtils.saveToPreferences(this, "adult_$i", "25-45")
        }
    }



    private fun navigateToNextActivity() {
        // Replace NextActivity::class.java with the actual activity you want to navigate to
        val intent = Intent(this, AnimalPreferenceActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToScanQRActivity() {
        // Replace ScanQRActivity::class.java with the actual activity for scanning QR codes
        val intent = Intent(this, ScanQRActivity::class.java)
        startActivity(intent)
    }
}