package com.samim.jarvis.voice.providers

import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository already added earlier; this file ensures simple access from orchestrator in case DI isn't used.
 */
class VoiceProviderRepository(private val secureStorage: SecureStorage) {

    private fun key(name: String, field: String) = "provider.$name.$field"

    suspend fun getApiKey(name: String): String? = withContext(Dispatchers.IO) {
        secureStorage.getString(key(name, "apikey"))?.ifEmpty { null }
    }

    suspend fun isEnabled(name: String): Boolean = withContext(Dispatchers.IO) {
        secureStorage.getString(key(name, "enabled"))?.toBoolean() ?: false
    }

    suspend fun getPriority(name: String): Int = withContext(Dispatchers.IO) {
        secureStorage.getString(key(name, "priority"))?.toIntOrNull() ?: 0
    }

    suspend fun listSupportedNames(): List<String> = withContext(Dispatchers.IO) {
        listOf(
            "ElevenLabs",
            "OpenAI",
            "GoogleCloudTTS",
            "AzureAI",
            "AmazonPolly",
            "Cartesia",
            "PlayHT",
            "Deepgram",
            "Coqui",
            "AndroidTTS"
        )
    }
}
