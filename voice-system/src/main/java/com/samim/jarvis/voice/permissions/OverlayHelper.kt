package com.samim.jarvis.voice.permissions

import android.content.Context
import android.provider.Settings

object OverlayHelper {
    fun canDrawOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
}
