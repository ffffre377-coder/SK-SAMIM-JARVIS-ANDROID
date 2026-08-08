package com.samim.jarvis.voice

import android.content.Context
import android.util.Log

/**
 * WakeWordManager: wrapper around PorcupineManager scaffold that exposes enable/disable, custom keyword, and lifecycle control.
 * This is a scaffold — real wake-word integration requires adding a native SDK and keyword models.
 */
class WakeWordManager(private val context: Context) {

    private val porcupine = PorcupineManager(context)
    private var enabled: Boolean = false
    private var keyword: String = "hey samim"

    interface Listener {
        fun onWakeWord()
    }

    private var listener: Listener? = null

    fun setListener(l: Listener) {
        this.listener = l
        porcupine.setListener(object : PorcupineManager.Listener {
            override fun onWakeWordDetected() {
                listener?.onWakeWord()
            }
        })
    }

    fun setKeyword(phrase: String) {
        keyword = phrase.trim().lowercase()
        Log.d("WakeWordManager", "setKeyword scaffold: $keyword")
    }

    fun start() {
        if (!enabled) return
        porcupine.start()
    }

    fun stop() {
        porcupine.stop()
    }

    fun enable(enabled: Boolean) {
        this.enabled = enabled
        if (enabled) start() else stop()
    }

    fun isEnabled(): Boolean = enabled
}
