package com.samim.jarvis

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JarvisApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            CrashHandler.install(this)
        } catch (e: Exception) {
        }
    }
}
