package com.example.dcis2.utility

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object HealthServicesUtils {
    private const val HEALTH_PERMISSION_REQUEST_CODE = 2000

    fun requestHealthServices(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.BODY_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.BODY_SENSORS),
                HEALTH_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted
            Toast.makeText(activity, "Health permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleHealthPermissionResult(
        requestCode: Int,
        grantResults: IntArray,
        activity: Activity
    ) {
        if (requestCode == HEALTH_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
                Toast.makeText(activity, "Health permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(activity, "Health permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
