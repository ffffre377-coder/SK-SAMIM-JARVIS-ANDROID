package com.samim.jarvis.voice.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun PermissionRationaleDialog(title: String, message: String, onRequest: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onRequest) { Text("Allow") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun RequestContactsPermission(onGranted: () -> Unit, onDenied: () -> Unit) {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) onGranted() else onDenied()
    }

    if (showRationale) {
        PermissionRationaleDialog(title = "Contacts Permission", message = "Jarvis needs access to your contacts to find people by name.", onRequest = {
            showRationale = false
            launcher.launch(Manifest.permission.READ_CONTACTS)
        }, onDismiss = { showRationale = false })
    }

    LaunchedEffect(Unit) {
        when (context.checkSelfPermission(Manifest.permission.READ_CONTACTS)) {
            PackageManager.PERMISSION_GRANTED -> onGranted()
            PackageManager.PERMISSION_DENIED -> showRationale = true
        }
    }
}

@Composable
fun RequestMediaPermission(onGranted: () -> Unit, onDenied: () -> Unit) {
    val context = LocalContext.current
    val readPerm = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
    var showRationale by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) onGranted() else onDenied()
    }

    if (showRationale) {
        PermissionRationaleDialog(title = "Media Permission", message = "Jarvis needs access to your photos and media to send or play media files.", onRequest = {
            showRationale = false
            launcher.launch(readPerm)
        }, onDismiss = { showRationale = false })
    }

    LaunchedEffect(Unit) {
        when (context.checkSelfPermission(readPerm)) {
            PackageManager.PERMISSION_GRANTED -> onGranted()
            PackageManager.PERMISSION_DENIED -> showRationale = true
        }
    }
}
