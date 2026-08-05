package com.samim.jarvis.voice.assistants

import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Simple Playlist repository persisted in SecureStorage as JSON.
 */
class PlaylistRepository(private val secureStorage: SecureStorage) {
    private fun key() = "playlists"

    suspend fun savePlaylist(name: String, uris: List<String>) {
        withContext(Dispatchers.IO) {
            val curr = loadAll().toMutableMap()
            curr[name] = uris
            secureStorage.putString(key(), Json.encodeToString(curr))
        }
    }

    suspend fun loadAll(): Map<String, List<String>> = withContext(Dispatchers.IO) {
        val raw = secureStorage.getString(key()) ?: return@withContext emptyMap()
        return@withContext try { Json.decodeFromString(raw) } catch (e: Exception) { emptyMap() }
    }

    suspend fun deletePlaylist(name: String) {
        withContext(Dispatchers.IO) {
            val curr = loadAll().toMutableMap()
            curr.remove(name)
            secureStorage.putString(key(), Json.encodeToString(curr))
        }
    }
}
