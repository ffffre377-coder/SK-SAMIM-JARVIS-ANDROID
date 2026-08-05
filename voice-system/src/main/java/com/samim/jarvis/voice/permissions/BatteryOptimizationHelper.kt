package com.samim.jarvis.voice.permissions

import android.content.Context
import android.os.Build
import android.os.PowerManager

object BatteryOptimizationHelper {
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) pm.isIgnoringBatteryOptimizations(context.packageName) else true
    }
}
