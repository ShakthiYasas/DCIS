package com.example.dcis2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class ScanQRActivity: AppCompatActivity()  {
    private lateinit var barcodeView: DecoratedBarcodeView
    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the barcode view
        barcodeView = findViewById(R.id.barcode_scanner)

        // Request camera permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            startScanning()
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission required for scanning QR codes.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startScanning() {
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                val qrData = result.text

                // Display the scanned result
                Toast.makeText(this@ScanQRActivity, "Scanned: ${result.text}", Toast.LENGTH_LONG).show()
                // You can handle the result further as needed

                // Start the DisplayQRDataActivity and pass the scanned data
                val intent = Intent(this@ScanQRActivity, DisplayQRDataActivity::class.java)
                intent.putExtra("qr_data", qrData)
                startActivity(intent)
            }

            override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {
                // Optional: Handle possible result points if necessary
            }
        })
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause() // Pause scanning when activity is paused
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume() // Resume scanning when activity is resumed
    }
}