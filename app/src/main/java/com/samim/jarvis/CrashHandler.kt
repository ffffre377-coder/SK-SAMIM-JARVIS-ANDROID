package com.samim.jarvis

import android.content.Context
import android.content.SharedPreferences
import java.io.PrintWriter
import java.io.StringWriter

object CrashHandler {

    private const val PREFS_NAME = "crash_prefs"
    private const val KEY_CRASH_LOG = "last_crash"

    fun install(context: Context) {
        val appContext = context.applicationContext
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))

                val prefs: SharedPreferences =
                    appContext.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                    )

                prefs.edit()
                    .putString(KEY_CRASH_LOG, sw.toString())
                    .commit()

            } catch (_: Exception) {
            }

            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    fun getLastCrash(context: Context): String? {
        val prefs = context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return prefs.getString(KEY_CRASH_LOG, null)
    }

    fun clearLastCrash(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        prefs.edit()
            .remove(KEY_CRASH_LOG)
            .apply()
    }
}
