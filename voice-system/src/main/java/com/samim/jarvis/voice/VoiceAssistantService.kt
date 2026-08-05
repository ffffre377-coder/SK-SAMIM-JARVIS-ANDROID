package com.samim.jarvis.voice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VoiceAssistantService : Service() {

    @Inject
    lateinit var wakeWordManager: WakeWordManager

    @Inject
    lateinit var sttManager: SpeechToTextManager

    companion object {
        const val ACTION_START = "com.samim.jarvis.voice.ACTION_START"
        const val ACTION_STOP = "com.samim.jarvis.voice.ACTION_STOP"
        const val NOTIF_CHANNEL_ID = "jarvis_voice_channel"
        const val NOTIF_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Hook wake-word event to start STT
        wakeWordManager.setListener(object : WakeWordManager.Listener {
            override fun onWakeWord() {
                Log.d("VoiceService", "Wake-word detected: starting STT")
                // Start listening with current language (default en-US) - ViewModel preferences control language selection
                sttManager.startListening()
            }
        })

        // Start manager if enabled
        if (wakeWordManager.isEnabled()) wakeWordManager.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startForegroundServiceWithNotification()
            ACTION_STOP -> stopSelf()
            else -> startForegroundServiceWithNotification()
        }
        return START_STICKY
    }

    private fun startForegroundServiceWithNotification() {
        // Start WakeWordManager (scaffold) and run as foreground service to improve reliability
        wakeWordManager.start()
        val notification = buildNotification()
        startForeground(NOTIF_ID, notification)
    }

    private fun buildNotification(): Notification {
        val stopIntent = Intent(this, VoiceAssistantService::class.java).apply { action = ACTION_STOP }
        val pendingStop = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("Jarvis Voice Assistant")
            .setContentText("Wake-word listening for 'Hey Samim'")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .addAction(android.R.drawable.ic_media_pause, "Stop", pendingStop)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Jarvis Voice"
            val descriptionText = "Voice assistant foreground service"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIF_CHANNEL_ID, name, importance).apply { description = descriptionText }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        wakeWordManager.stop()
        sttManager.release()
    }
}
