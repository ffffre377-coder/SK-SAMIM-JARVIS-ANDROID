package com.samim.jarvis.voice.assistants

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MediaAssistant {
    suspend fun searchMedia(query: String): List<Uri>
    suspend fun playMedia(uri: Uri)
    suspend fun shareMedia(uri: Uri)
    suspend fun createPlaylist(name: String, items: List<Uri>)
}

class MediaAssistantImpl(private val context: Context) : MediaAssistant {

    override suspend fun searchMedia(query: String): List<Uri> = withContext(Dispatchers.IO) {
        val results = mutableListOf<Uri>()
        try {
            results += queryMediaStore(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, query)
            results += queryMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, query)
            results += queryMediaStore(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, query)
        } catch (e: Exception) {
            Log.e("MediaAssistant", "searchMedia failed", e)
        }
        return@withContext results
    }

    private fun queryMediaStore(uri: Uri, query: String): List<Uri> {
        val res = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            val idIdx = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idIdx)
                val contentUri = Uri.withAppendedPath(uri, id.toString())
                res.add(contentUri)
            }
        }
        return res
    }

    override suspend fun playMedia(uri: Uri) {
        withContext(Dispatchers.Main) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, context.contentResolver.getType(uri))
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("MediaAssistant", "playMedia failed", e)
            }
        }
    }

    override suspend fun shareMedia(uri: Uri) {
        withContext(Dispatchers.Main) {
            val share = Intent(Intent.ACTION_SEND).apply {
                type = context.contentResolver.getType(uri) ?: "*/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(share, "Share media").apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        }
    }

    override suspend fun createPlaylist(name: String, items: List<Uri>) {
        // Simple placeholder: just store playlist as JSON in SecureStorage later (not implemented here)
    }
}
