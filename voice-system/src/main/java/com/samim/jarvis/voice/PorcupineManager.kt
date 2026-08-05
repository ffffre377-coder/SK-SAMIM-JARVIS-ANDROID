package com.samim.jarvis.voice

import android.content.Context
import android.util.Log

// PorcupineManager is a scaffold for Porcupine wake-word integration (Hey Jarvis).
// The real integration requires the Porcupine SDK and model files; this class provides
// the lifecycle and callback hooks to wire into the app once the SDK and models are added.
class PorcupineManager(private val context: Context) {

    interface Listener {
        fun onWakeWordDetected()
    }

    private var listener: Listener? = null
    private var running = false

    fun setListener(l: Listener) {
        listener = l
    }

    fun start() {
        // TODO: Initialize Porcupine engine and start audio capture
        running = true
        Log.d("Porcupine", "start scaffold - integrate Porcupine SDK and models")
    }

    fun stop() {
        // TODO: Stop Porcupine engine and release resources
        running = false
        Log.d("Porcupine", "stop scaffold")
    }

    fun isRunning(): Boolean = running
}
