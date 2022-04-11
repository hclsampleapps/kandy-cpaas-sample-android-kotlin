package com.hcl.kandy.cpaas.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class MarshMallowPermission(activity: Activity) {
    var activity: Activity
    fun checkAllPermissions(): Boolean {
        val result1: Int =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.MODIFY_AUDIO_SETTINGS)
        val result2: Int =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
        val result3: Int =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        return if (result1 == PackageManager.PERMISSION_GRANTED
            && result2 == PackageManager.PERMISSION_GRANTED
            && result3 == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestALLPermissions() {
        val PERMISSIONS = arrayOf(
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        if (activity.shouldShowRequestPermissionRationale(PERMISSIONS[0])
            && activity.shouldShowRequestPermissionRationale(PERMISSIONS[1])
            && activity.shouldShowRequestPermissionRationale(PERMISSIONS[2])
        ) {
            Toast.makeText(
                activity, "Permission required",
                Toast.LENGTH_LONG
            ).show()
        } else {
            activity.requestPermissions(PERMISSIONS, PERMISSION_ALL_REQUEST_CODE)
        }
    }

    companion object {
        const val PERMISSION_ALL_REQUEST_CODE = 9
    }

    init {
        this.activity = activity
    }
}