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

    suspend fun getConfig(name: String): ProviderConfig = withContext(Dispatchers.IO) {
        ProviderConfig(
            name = name,
            apiKey = getApiKey(name),
            enabled = isEnabled(name),
            voiceId = secureStorage.getString(key(name, "voiceid"))?.ifEmpty { null },
            priority = getPriority(name)
        )
    }

    suspend fun saveConfig(config: ProviderConfig) = withContext(Dispatchers.IO) {
        secureStorage.putString(key(config.name, "apikey"), config.apiKey ?: "")
        secureStorage.putString(key(config.name, "enabled"), config.enabled.toString())
        secureStorage.putString(key(config.name, "voiceid"), config.voiceId ?: "")
        secureStorage.putString(key(config.name, "priority"), config.priority.toString())
    }

    suspend fun deleteConfig(name: String) = withContext(Dispatchers.IO) {
        secureStorage.putString(key(name, "apikey"), "")
        secureStorage.putString(key(name, "enabled"), "false")
        secureStorage.putString(key(name, "voiceid"), "")
        secureStorage.putString(key(name, "priority"), "0")
    }

    suspend fun listSupportedProviders(): List<ProviderConfig> = withContext(Dispatchers.IO) {
        listSupportedNames().map { getConfig(it) }
    }
}
