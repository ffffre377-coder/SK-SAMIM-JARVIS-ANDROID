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
        // Example integration steps (not implemented here):
        // 1) Load Porcupine native library and keyword model
        // 2) Create Porcupine instance with the keyword "hey jarvis" model
        // 3) Start recording audio (AudioRecord) and pass frames into Porcupine
        // 4) On detection, call listener?.onWakeWordDetected()
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
