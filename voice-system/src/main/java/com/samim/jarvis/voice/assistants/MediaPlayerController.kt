package com.samim.jarvis.voice.assistants

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

/**
 * Simple in-app media player controller for playlists.
 */
class MediaPlayerController(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var playlist: List<Uri> = emptyList()
    private var currentIndex = 0

    fun setPlaylist(items: List<Uri>, startIndex: Int = 0) {
        playlist = items
        currentIndex = startIndex.coerceIn(0, playlist.size.coerceAtLeast(1) - 1)
        playCurrent()
    }

    private fun playCurrent() {
        stop()
        if (playlist.isEmpty()) return
        val uri = playlist[currentIndex]
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, uri)
            setOnCompletionListener {
                next()
            }
            prepare()
            start()
        }
    }

    fun play() { mediaPlayer?.start() }
    fun pause() { mediaPlayer?.pause() }
    fun stop() { mediaPlayer?.stop(); mediaPlayer?.release(); mediaPlayer = null }

    fun next() {
        if (playlist.isEmpty()) return
        currentIndex = (currentIndex + 1) % playlist.size
        playCurrent()
    }

    fun previous() {
        if (playlist.isEmpty()) return
        currentIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
        playCurrent()
    }
}
