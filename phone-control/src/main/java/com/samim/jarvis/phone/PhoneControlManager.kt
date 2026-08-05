package com.samim.jarvis.phone

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.widget.Toast

class PhoneControlManager(private val context: Context) {

    /**
     * Attempts to launch another app by package name.
     * Returns true if the launch Intent was found and started.
     */
    fun openApp(packageName: String): Boolean {
        val pm: PackageManager = context.packageManager
        val launchIntent: Intent? = pm.getLaunchIntentForPackage(packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            true
        } else {
            Toast.makeText(context, "App not installed: $packageName", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Sets the stream volume. streamType defaults to STREAM_MUSIC.
     * level is clamped between 0 and the stream max.
     */
    fun setVolume(level: Int, streamType: Int = AudioManager.STREAM_MUSIC) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(streamType)
        val vol = level.coerceIn(0, max)
        am.setStreamVolume(streamType, vol, AudioManager.FLAG_SHOW_UI)
    }
}
