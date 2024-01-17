package com.example.nfcreader.ui

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.nfcreader.Constants
import com.example.nfcreader.R
import com.example.nfcreader.models.TagInfo

class QRActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private val REQUEST_CAMERA_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qractivity)

        val scannerView = findViewById<CodeScannerView>(R.id.codeScannerView)

        codeScanner = CodeScanner(this, scannerView)


        if (checkPermission()) {
            codeScanner.startPreview()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        val tagInfoArray = arrayOf(
            TagInfo("111", false, "user1"),
            TagInfo("222", false, "user2"),
            TagInfo("333", false, "user3"),
            TagInfo("444", false, "user4"),
            TagInfo("555", false, "user5"),
            TagInfo("666", false, "user6"),
            TagInfo("777", false, "user7"),
            TagInfo("888", false, "user8"),
            TagInfo("999", false, "user9")
        )

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback { result ->
            runOnUiThread {
                val scannedUrl = result.text
                if (scannedUrl.startsWith(Constants.k_BASE_URL)) {
                    val extractedData = scannedUrl.substring(Constants.k_BASE_URL.length)

                    val matchingTagInfo = tagInfoArray.find { it.tagId == extractedData }

                    if (matchingTagInfo != null) {
                        if (!matchingTagInfo.status) {
                            matchingTagInfo.status = true
                            Toast.makeText(this, "Tag assigned successfully", Toast.LENGTH_SHORT).show()
                        }
                        if(matchingTagInfo.status==true) {
                            Toast.makeText(this, "Tag is already assigned", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Tag not found in the array", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid QR code. URL does not match the expected base URL.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    }
