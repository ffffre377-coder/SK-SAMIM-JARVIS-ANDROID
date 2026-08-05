package com.samim.jarvis.vision

import android.content.Context

/**
 * ScreenshotManager scaffold: helpers to capture or receive screenshots for analysis.
 * Real implementations must request appropriate permissions and use MediaProjection or other APIs.
 */
class ScreenshotManager(private val context: Context) {
    fun requestScreenshotPermission() {
        // TODO: Launch MediaProjection permission flow
    }

    fun captureScreenshot(): String? {
        // TODO: implement screenshot capture and return file path
        return null
    }
}
