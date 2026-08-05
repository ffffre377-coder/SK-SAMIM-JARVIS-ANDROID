package com.samim.jarvis.voice

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class VoiceAssistantService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("VoiceAssistant", "Service started")
        // TODO: manage wake-word and STT lifecycle
        return START_STICKY
    }
}
