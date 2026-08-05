package com.samim.jarvis.voice.permissions

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

/**
 * Minimal AccessibilityService stub — it should be declared in AndroidManifest and enabled by the user explicitly.
 * This service must NOT perform actions without explicit user consent. Use it only to provide accessibility features
 * when the user enables and configures them in Settings.
 */
class JarvisAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No-op: do not auto-act. Implement optional assistive UI only after explicit user opt-in in settings.
        Log.d("JarvisA11y", "event: $event")
    }

    override fun onInterrupt() {
        Log.d("JarvisA11y", "interrupted")
    }
}
