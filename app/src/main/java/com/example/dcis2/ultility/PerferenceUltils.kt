package com.example.dcis2.ultility

import android.content.Context
import android.content.SharedPreferences

object PreferencesUtils {

    fun saveToPreferences(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
}
