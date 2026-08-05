package com.samim.jarvis.voice

import android.content.Context
import android.content.pm.PackageManager

object PermissionUtils {
    fun hasPermission(context: Context, perm: String): Boolean {
        return context.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED
    }
}
