package com.samim.jarvis.voice

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * WhatsAppHelper: helper methods to send text or media to WhatsApp.
 * Uses Intents targeted at the WhatsApp package when available.
 */
object WhatsAppHelper {
    private const val WHATSAPP_PACKAGE = "com.whatsapp"

    fun openWhatsApp(context: Context) {
        val pm = context.packageManager
        val launch = pm.getLaunchIntentForPackage(WHATSAPP_PACKAGE)
        if (launch != null) {
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launch)
        } else {
            // Fallback: open Play Store page
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$WHATSAPP_PACKAGE")).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(intent)
        }
    }

    fun sendImageToWhatsApp(context: Context, imageUri: Uri) {
        try {
            val share = Intent(Intent.ACTION_SEND).apply {
                type = context.contentResolver.getType(imageUri) ?: "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                setPackage(WHATSAPP_PACKAGE)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(share)
        } catch (e: Exception) {
            Log.e("WhatsAppHelper", "sendImageToWhatsApp failed", e)
        }
    }

    fun sendTextToContactViaApi(context: Context, phoneNumber: String, text: String) {
        // Use wa.me URL scheme
        try {
            val url = "https://api.whatsapp.com/send?phone=${phoneNumber}&text=${Uri.encode(text)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("WhatsAppHelper", "sendTextToContact failed", e)
        }
    }
}
