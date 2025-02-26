package com.example.dcis2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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
        val profileData = JSONObject()
        profileData.put("adult_1", "25-45")
        profileData.put("adult_2", "25-45")
        profileData.put("child_1", "5-10")
        profileData.put("child_2", "0-5")
        profileData.put("Number of Adults", 2)
        profileData.put("Number of Children", 2)
        saveProfileData(profileData)
    }

    private fun saveIndividualProfile() {
        val profileData = JSONObject()
        profileData.put("adult_1", "18-25")

        saveProfileData(profileData)
    }

    private fun saveGroupProfile() {
        val profileData = JSONObject()
        for (i in 1..10) {
            profileData.put("adult_$i", "25-45")
        }

        saveProfileData(profileData)
    }

    private fun saveProfileData(profileData: JSONObject) {
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("profile_data", profileData.toString())
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