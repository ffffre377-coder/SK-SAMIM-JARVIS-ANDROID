package com.samim.jarvis.voice.assistants

import android.content.Context
import android.net.Uri

interface FileAssistant {
    suspend fun searchFiles(query: String): List<Uri>
    suspend fun openFile(uri: Uri)
    suspend fun shareFile(uri: Uri)
}
