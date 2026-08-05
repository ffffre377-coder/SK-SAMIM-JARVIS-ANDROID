package com.samim.jarvis.voice.ui

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samim.jarvis.voice.permissions.PermissionManager
import com.samim.jarvis.voice.permissions.OverlayHelper
import com.samim.jarvis.voice.permissions.BatteryOptimizationHelper
import com.samim.jarvis.security.SecureStorage

@Composable
fun PermissionSettingsScreen(context: Context, activity: Activity, secureStorage: SecureStorage, onToggleChanged: (String, Boolean) -> Unit) {
    Column(modifier = Modifier.padding(12.dp)) {
        TopAppBar(title = { Text("Assistant Permissions & Controls") })

        PermissionToggleRow(label = "Accessibility Service", key = "perm_accessibility", secureStorage = secureStorage, onClick = {
            // Open accessibility settings for the user to enable service
            PermissionManager.openAccessibilitySettings(context)
        }, onToggle = onToggleChanged)

        PermissionToggleRow(label = "Display over other apps", key = "perm_overlay", secureStorage = secureStorage, onClick = {
            PermissionManager.openOverlaySettings(context)
        }, onToggle = onToggleChanged)

        PermissionToggleRow(label = "Microphone (voice/wake-word)", key = "perm_microphone", secureStorage = secureStorage, onClick = {
            PermissionManager.requestMicrophonePermission(activity, 1001)
        }, onToggle = onToggleChanged)

        PermissionToggleRow(label = "Notifications (background status)", key = "perm_notifications", secureStorage = secureStorage, onClick = {
            PermissionManager.requestNotificationPermission(activity, 1002)
        }, onToggle = onToggleChanged)

        PermissionToggleRow(label = "Ignore battery optimization", key = "perm_battery", secureStorage = secureStorage, onClick = {
            PermissionManager.openIgnoreBatteryOptimizationsSettings(context)
        }, onToggle = onToggleChanged)

        PermissionToggleRow(label = "Storage / Media access", key = "perm_storage", secureStorage = secureStorage, onClick = {
            PermissionManager.requestStoragePermission(activity, 1003)
        }, onToggle = onToggleChanged)

        PermissionToggleRow(label = "Contacts access", key = "perm_contacts", secureStorage = secureStorage, onClick = {
            PermissionManager.requestContactsPermission(activity, 1004)
        }, onToggle = onToggleChanged)
    }
}

@Composable
fun PermissionToggleRow(label: String, key: String, secureStorage: SecureStorage, onClick: () -> Unit, onToggle: (String, Boolean) -> Unit) {
    var enabled by remember { mutableStateOf(secureStorage.getString(key) == "true") }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label)
        RowWithSwitch(enabled = enabled, onToggle = {
            enabled = it
            secureStorage.putString(key, it.toString())
            onToggle(key, it)
        }, onButtonClick = onClick)
    }
}

@Composable
fun RowWithSwitch(enabled: Boolean, onToggle: (Boolean) -> Unit, onButtonClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onButtonClick, modifier = Modifier.fillMaxWidth()) { Text("Configure") }
        androidx.compose.material.Switch(checked = enabled, onCheckedChange = onToggle)
    }
}
