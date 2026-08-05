package com.samim.jarvis.voice.assistants

import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface SnippetRepository {
    suspend fun saveSnippet(title: String, code: String): String
    suspend fun listSnippets(): List<Pair<String, String>>
    suspend fun getSnippet(id: String): String?
    suspend fun deleteSnippet(id: String)
}

class SecureSnippetRepository(private val secureStorage: SecureStorage) : SnippetRepository {

    private fun keyForId(id: String) = "snippet.$id"
    private fun registryKey() = "snippet._registry"

    override suspend fun saveSnippet(title: String, code: String): String = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        secureStorage.putString(keyForId(id), "$title:::$code")
        // update registry
        val reg = secureStorage.getString(registryKey()) ?: ""
        val updated = if (reg.isBlank()) id else "$reg;$id"
        secureStorage.putString(registryKey(), updated)
        return@withContext id
    }

    override suspend fun listSnippets(): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        val reg = secureStorage.getString(registryKey()) ?: ""
        if (reg.isBlank()) return@withContext emptyList()
        val ids = reg.split(";").filter { it.isNotBlank() }
        val out = mutableListOf<Pair<String, String>>()
        for (id in ids) {
            val raw = secureStorage.getString(keyForId(id)) ?: continue
            val parts = raw.split(":::")
            if (parts.size >= 2) out.add(Pair(parts[0], parts[1]))
        }
        return@withContext out
    }

    override suspend fun getSnippet(id: String): String? = withContext(Dispatchers.IO) {
        val raw = secureStorage.getString(keyForId(id)) ?: return@withContext null
        val parts = raw.split(":::")
        return@withContext if (parts.size >= 2) parts[1] else null
    }

    override suspend fun deleteSnippet(id: String) = withContext(Dispatchers.IO) {
        secureStorage.putString(keyForId(id), "")
        val reg = secureStorage.getString(registryKey()) ?: ""
        val updated = reg.split(";").filter { it.isNotBlank() && it != id }.joinToString(";")
        secureStorage.putString(registryKey(), updated)
    }
}
