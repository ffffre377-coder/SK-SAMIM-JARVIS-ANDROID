package com.samim.jarvis.voice

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sqrt

class TtsPlayback(private val context: Context) {

    interface Listener {
        fun onStart()
        fun onProgress(level: Float)
        fun onComplete()
    }

    private var mediaPlayer: MediaPlayer? = null
    private var listener: Listener? = null
    private var progressJob: Job? = null

    fun setListener(l: Listener?) {
        listener = l
    }

    fun play(bytes: ByteArray) {
        try {
            // Pre-scan audio bytes for amplitude envelope (basic RMS over windows)
            val envelope = computeEnvelope(bytes, windowSize = 2048)

            val file = File.createTempFile("tts", ".mp3", context.cacheDir)
            val fos = FileOutputStream(file)
            fos.write(bytes)
            fos.flush()
            fos.close()

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener {
                    // notify start
                    listener?.onStart()
                    start()
                    // schedule progress updates based on duration and envelope
                    val duration = duration.coerceAtLeast(100)
                    progressJob?.cancel()
                    progressJob = CoroutineScope(Dispatchers.Main).launch {
                        val sampleCount = envelope.size
                        val interval = duration / (sampleCount.coerceAtLeast(1))
                        for (i in 0 until sampleCount) {
                            if (!isPlaying) break
                            listener?.onProgress(envelope[i])
                            delay(interval.toLong())
                        }
                    }
                }
                setOnCompletionListener {
                    progressJob?.cancel()
                    listener?.onComplete()
                    it.release()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        progressJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // ignore
        }
        mediaPlayer = null
    }

    private fun computeEnvelope(bytes: ByteArray, windowSize: Int): FloatArray {
        // Very basic approximation: treat bytes as unsigned PCM magnitude estimate — not accurate for mp3 but provides a rhythm
        if (bytes.isEmpty()) return FloatArray(1) { 0f }
        val windows = mutableListOf<Float>()
        var i = 0
        while (i < bytes.size) {
            val end = (i + windowSize).coerceAtMost(bytes.size)
            var sum = 0.0
            for (j in i until end) {
                sum += (bytes[j].toInt() and 0xFF)
            }
            val mean = sum / (end - i)
            // normalize to 0..1
            windows.add((mean / 255.0).toFloat())
            i += windowSize
        }
        // smooth and return
        return windows.map { it.coerceIn(0f, 1f) }.toFloatArray()
    }
}
