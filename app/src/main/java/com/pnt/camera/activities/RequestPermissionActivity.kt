package com.pnt.camera.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pnt.camera.R
import com.pnt.camera.databinding.ActivityRequestPermissionBinding

class RequestPermissionActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRequestPermissionBinding.inflate(layoutInflater)
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 101
        private const val PERMISSION_CAMERA = android.Manifest.permission.CAMERA
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.requestPermissionButton.setOnClickListener {
            requestPermission()
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this@RequestPermissionActivity, PERMISSION_CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                this@RequestPermissionActivity, "Permission accepted", Toast.LENGTH_LONG
            ).show()

            startActivity(Intent(this@RequestPermissionActivity, MainActivity::class.java))
            finish()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@RequestPermissionActivity, PERMISSION_CAMERA
            )
        ) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@RequestPermissionActivity, R.style.DialogTheme)
            builder.setMessage("This app requires CAMERA permission for particular feature to work as expected.")
                .setTitle("Permission Required").setCancelable(false).setPositiveButton("OK") { dialog, _ ->
                    ActivityCompat.requestPermissions(
                        this@RequestPermissionActivity, arrayOf(PERMISSION_CAMERA), CAMERA_REQUEST_CODE
                    )
                    dialog.dismiss()
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            builder.show()
        } else {
            ActivityCompat.requestPermissions(
                this@RequestPermissionActivity, arrayOf(PERMISSION_CAMERA), CAMERA_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this@RequestPermissionActivity, MainActivity::class.java))
                finish()
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this@RequestPermissionActivity, PERMISSION_CAMERA
                )
            ) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@RequestPermissionActivity, R.style.DialogTheme)
                builder.setMessage("This feature is unavailable because this feature requires permission that you have denied. Please allow Camera permission from settings to proceed further.")
                    .setTitle("Permission Required").setCancelable(false).setPositiveButton("Settings") { dialog, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.setData(uri)
                        startActivity(intent)

                        dialog.dismiss()
                    }.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                builder.show()
            } else {
                requestPermission()
            }
        }
    }
}