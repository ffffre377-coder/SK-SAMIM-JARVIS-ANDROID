package com.samim.jarvis.voice

import android.content.Context
import android.net.Uri
import android.util.Log

/**
 * ScreenUnderstandingManager: uses the Accessibility service (if enabled) to obtain a lightweight
 * description of the current screen to help with confirmation dialogs in the voice flow.
 */
object ScreenUnderstandingManager {

    fun captureScreenDescription(): String {
        val svc = JarvisAccessibilityService.instance
        return if (svc != null) {
            try {
                svc.describeCurrentScreen()
            } catch (e: Exception) {
                Log.e("ScreenUnderstanding", "capture failed", e)
                "(screen description unavailable)"
            }
        } else {
            "(accessibility service not enabled)"
        }
    }
}
