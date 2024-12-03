package com.example.dcis2.ultility

import android.content.Context
import android.content.SharedPreferences

object PreferencesUtils {
    private const val PREF_NAME = "UserPreferences"

    fun saveToPreferences(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    // Function to clear and reset SharedPreferences
    fun resetPreferences(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Clear all stored data
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

}
