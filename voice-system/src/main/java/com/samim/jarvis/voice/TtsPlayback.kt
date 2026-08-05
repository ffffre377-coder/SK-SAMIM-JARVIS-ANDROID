package com.samim.jarvis.voice

import android.content.Context
import android.media.MediaPlayer
import java.io.File
import java.io.FileOutputStream

class TtsPlayback(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun play(bytes: ByteArray) {
        try {
            val file = File.createTempFile("tts", ".mp3", context.cacheDir)
            val fos = FileOutputStream(file)
            fos.write(bytes)
            fos.flush()
            fos.close()

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnCompletionListener {
                    it.release()
                }
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
