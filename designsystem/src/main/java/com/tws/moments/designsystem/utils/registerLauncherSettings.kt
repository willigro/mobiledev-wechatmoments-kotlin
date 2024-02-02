package com.tws.moments.designsystem.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat

fun MutableList<String>.arePermissionsGrated(context: Context): Boolean {
    for (permission in this) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }

    return true
}

fun ComponentActivity.retrieveSettingsIntent() = Intent(
    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
    Uri.parse("package:${packageName}")
)

@Composable
fun registerLauncherSettings(
    permissions: MutableList<String>,
    componentActivity: ComponentActivity,
    permissionIsGranted: (Boolean) -> Unit,
) = rememberLauncherForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) {
    permissionIsGranted(permissions.arePermissionsGrated(componentActivity))
}