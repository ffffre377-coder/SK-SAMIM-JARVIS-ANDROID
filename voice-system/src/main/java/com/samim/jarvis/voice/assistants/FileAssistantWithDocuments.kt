package com.samim.jarvis.voice.assistants

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Extend FileAssistantImpl to also search Downloads/MediaStore.Downloads for documents (PDFs, docs).
 */
class FileAssistantWithDocuments(private val context: Context) : FileAssistant {

    override suspend fun searchFiles(query: String): List<Uri> = withContext(Dispatchers.IO) {
        val results = mutableListOf<Uri>()
        try {
            // Images, videos, audio
            results += FileAssistantImpl(context).searchFiles(query)

            // Search Downloads / documents (PDF/Doc)
            val downloadsUri = android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.MIME_TYPE)
            val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} LIKE ?"
            val selectionArgs = arrayOf("%$query%")
            val cursor: Cursor? = context.contentResolver.query(downloadsUri, projection, selection, selectionArgs, null)
            cursor?.use {
                val idIdx = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                while (it.moveToNext()) {
                    val id = it.getLong(idIdx)
                    val contentUri = Uri.withAppendedPath(downloadsUri, id.toString())
                    results.add(contentUri)
                }
            }
        } catch (e: Exception) {
            Log.e("FileAssistantDocs", "searchFiles failed", e)
        }
        return@withContext results
    }

    override suspend fun openFile(uri: Uri) {
        FileAssistantImpl(context).openFile(uri)
    }

    override suspend fun shareFile(uri: Uri) {
        FileAssistantImpl(context).shareFile(uri)
    }
}
