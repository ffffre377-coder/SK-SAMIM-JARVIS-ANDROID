package com.samim.jarvis.voice

import android.content.Context
import android.content.Intent
import android.util.Log
import com.samim.jarvis.voice.permissions.PermissionManager

/**
 * BackgroundReliabilityHelper: small helpers to improve background reliability and guide the user
 * to grant battery exemptions and to start the assistant as a foreground service when needed.
 */
object BackgroundReliabilityHelper {

    fun ensureForegroundService(context: Context) {
        try {
            val intent = Intent(context, VoiceAssistantService::class.java).apply { action = VoiceAssistantService.ACTION_START }
            context.startForegroundService(intent)
        } catch (e: Exception) {
            Log.e("BackgroundHelper", "ensureForegroundService failed", e)
            // Best-effort; starting foreground service may require additional manifest config
        }
    }

    fun openBatteryOptimizationSettings(context: Context) {
        PermissionManager.openIgnoreBatteryOptimizationsSettings(context)
    }
}
