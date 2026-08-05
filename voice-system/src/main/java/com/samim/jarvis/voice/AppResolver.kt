package com.samim.jarvis.voice

import android.content.Context
import android.content.pm.PackageManager

/**
 * AppResolver maps a friendly app name to an installed package name using PackageManager.
 */
object AppResolver {
    fun resolvePackageForAppName(context: Context, appName: String): String? {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val lower = appName.lowercase()
        // Try exact match on app label
        val match = apps.firstOrNull { pm.getApplicationLabel(it).toString().lowercase().contains(lower) }
        return match?.packageName
    }
}
