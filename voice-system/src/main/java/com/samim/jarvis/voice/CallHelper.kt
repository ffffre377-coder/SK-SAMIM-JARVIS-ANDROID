package com.samim.jarvis.voice

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object CallHelper {
    /**
     * Place a call to the phone number. If CALL_PHONE permission is granted, performs ACTION_CALL.
     * Otherwise opens the dialer with ACTION_DIAL so the user manually initiates the call.
     */
    fun placeCall(context: Context, phoneNumber: String) {
        try {
            val sanitized = phoneNumber.replace("\\s".toRegex(), "")
            val uri = Uri.parse("tel:$sanitized")
            val pm = context.packageManager
            val callIntent = Intent(Intent.ACTION_CALL, uri)
            val dialIntent = Intent(Intent.ACTION_DIAL, uri)
            // If CALL_PHONE permission is granted, attempt to call directly; otherwise open dialer
            if (PermissionUtils.hasPermission(context, android.Manifest.permission.CALL_PHONE)) {
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(callIntent)
            } else {
                // open dialer as a privacy-friendly fallback
                dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(dialIntent)
            }
        } catch (e: Exception) {
            Log.e("CallHelper", "placeCall failed", e)
        }
    }
}
