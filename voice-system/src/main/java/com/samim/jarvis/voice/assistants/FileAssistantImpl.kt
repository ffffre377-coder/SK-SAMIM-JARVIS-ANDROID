package com.samim.jarvis.voice.assistants

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Simple FileAssistant implementation: searches common media stores and files by display name.
 * This is a best-effort, provider-agnostic implementation. For production, expand to DocumentsContract.
 */
class FileAssistantImpl(private val context: Context) : FileAssistant {

    override suspend fun searchFiles(query: String): List<Uri> = withContext(Dispatchers.IO) {
        val results = mutableListOf<Uri>()
        try {
            // Search images
            results += queryMediaStore(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, query)
            // Search videos
            results += queryMediaStore(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, query)
            // Search audio
            results += queryMediaStore(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, query)
            // Search general files via MediaStore.Files
            results += queryMediaStore(MediaStore.Files.getContentUri("external"), query)
        } catch (e: Exception) {
            Log.e("FileAssistant", "searchFiles failed", e)
        }
        return@withContext results
    }

    private fun queryMediaStore(uri: Uri, query: String): List<Uri> {
        val res = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
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

    override suspend fun openFile(uri: Uri) {
        withContext(Dispatchers.Main) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(intent)
        }
    }

    override suspend fun shareFile(uri: Uri) {
        withContext(Dispatchers.Main) {
            val share = Intent(Intent.ACTION_SEND).apply {
                type = context.contentResolver.getType(uri) ?: "*/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(share, "Share file").apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        }
    }
}
