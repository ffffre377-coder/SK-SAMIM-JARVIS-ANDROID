package com.samim.jarvis.voice.ui

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samim.jarvis.voice.permissions.PermissionManager
import com.samim.jarvis.security.SecureStorage

/**
 * Accessibility onboarding Activity content.
 * This screen explains what the accessibility service can do and guides the user step-by-step.
 */
class AccessibilityOnboardingActivity : ComponentActivity() {
    // Empty activity — Compose UI should be hosted by the app's activity/compose setup.
}

@Composable
fun AccessibilityOnboardingScreen(context: Context, secureStorage: SecureStorage) {
    Column(modifier = Modifier.padding(12.dp)) {
        TopAppBar(title = { Text("Accessibility Onboarding") })
        Text("Why enable Accessibility for JARVIS")
        Text("\nAccessibility powers optional screen understanding features, such as:\n- Screen reading: describe visible text to assist confirmation\n- Assisted navigation: help find and highlight buttons you asked to interact with\n- Button detection: detect primary buttons on the current screen\n- Voice guided actions: assist you to complete flows with your approval")
        Text("\nImportant privacy notes")
        Text("JARVIS will never perform actions automatically. You must explicitly enable each assistive feature and confirm any action. Accessibility is used only to help with confirmations and to provide assistive UI.")
        Text("\nSteps to enable")
        Text("1) Tap 'Open Accessibility Settings' and enable Jarvis Accessibility Service.\n2) Return here and enable the specific features you want (toggles).\n3) Use the Configuration buttons to fine-tune behavior.")

        Button(onClick = { PermissionManager.openAccessibilitySettings(context) }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text("Open Accessibility Settings")
        }

        // Feature toggles stored in SecureStorage
        FeatureToggleRow(label = "Screen reading", key = "a11y_screen_reading", secureStorage = secureStorage)
        FeatureToggleRow(label = "Assisted navigation", key = "a11y_assisted_nav", secureStorage = secureStorage)
        FeatureToggleRow(label = "Button detection", key = "a11y_button_detect", secureStorage = secureStorage)
        FeatureToggleRow(label = "Voice guided actions", key = "a11y_voice_actions", secureStorage = secureStorage)

        Button(onClick = { /* Optionally start a quick demo or run a diagnostic */ }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Run onboarding diagnostic")
        }
    }
}

@Composable
fun FeatureToggleRow(label: String, key: String, secureStorage: SecureStorage) {
    var checked by remember {
        mutableStateOf(secureStorage.getString(key)?.toBoolean() ?: false)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.padding(end = 8.dp))
        Switch(
            checked = checked,
            onCheckedChange = { newValue ->
                checked = newValue
                secureStorage.putString(key, newValue.toString())
            }
        )
    }
}
