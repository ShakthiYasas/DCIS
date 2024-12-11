package com.example.dcis2.utility

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID
import android.content.pm.PackageManager
import org.json.JSONObject

object BluetoothUtils {
    private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 1000
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    fun requestBluetoothPermissions(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request necessary Bluetooth permissions
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        } else {
            Toast.makeText(activity, "Bluetooth permissions already granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleBluetoothPermissionResult(
        requestCode: Int,
        grantResults: IntArray,
        activity: Activity
    ) {
        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Bluetooth permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun enableBluetooth(activity: Activity) {
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = android.content.Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, BLUETOOTH_PERMISSION_REQUEST_CODE)
        } else {
            Toast.makeText(activity, "Bluetooth is already enabled", Toast.LENGTH_SHORT).show()
        }
    }

    fun connectToDevice(device: BluetoothDevice, uuid: UUID, activity: Activity): BluetoothSocket? {
        return try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request necessary Bluetooth permissions
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    BLUETOOTH_PERMISSION_REQUEST_CODE
                )
            }
            socket.connect()
            socket
        } catch (e: IOException) {
            Log.e("BluetoothUtils", "Failed to connect: ${e.message}")
            null
        }
    }

    fun sendJsonData(socket: BluetoothSocket, jsonData: JSONObject) {
        try {
            val outputStream = socket.outputStream
            val jsonString = jsonData.toString()
            val jsonBytes = jsonString.toByteArray(Charsets.UTF_8)
            outputStream.write(jsonBytes)
            outputStream.flush()
            Log.d("BluetoothUtils", "Data sent: $jsonData")
        } catch (e: IOException) {
            Log.e("BluetoothUtils", "Failed to send data: ${e.message}")
        }
    }
}
