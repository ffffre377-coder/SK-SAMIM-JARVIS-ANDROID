package com.samim.jarvis.voice.providers

import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Simple repository that stores provider API keys and settings securely using SecureStorage.
 * Uses key prefixes to avoid requiring a JSON serializer dependency.
 */
class VoiceProviderRepository(private val secureStorage: SecureStorage) {

    private fun key(name: String, field: String) = "provider.$name.$field"

    suspend fun saveConfig(config: ProviderConfig) {
        withContext(Dispatchers.IO) {
            secureStorage.putString(key(config.name, "apikey"), config.apiKey ?: "")
            secureStorage.putString(key(config.name, "enabled"), config.enabled.toString())
            secureStorage.putString(key(config.name, "voiceId"), config.voiceId ?: "")
            secureStorage.putString(key(config.name, "priority"), config.priority.toString())
        }
    }

    suspend fun deleteConfig(name: String) {
        withContext(Dispatchers.IO) {
            secureStorage.putString(key(name, "apikey"), "")
            secureStorage.putString(key(name, "enabled"), "false")
            secureStorage.putString(key(name, "voiceId"), "")
            secureStorage.putString(key(name, "priority"), "0")
        }
    }

    suspend fun getConfig(name: String): ProviderConfig = withContext(Dispatchers.IO) {
        val apiKey = secureStorage.getString(key(name, "apikey"))
        val enabled = secureStorage.getString(key(name, "enabled"))?.toBoolean() ?: false
        val voiceId = secureStorage.getString(key(name, "voiceId"))
        val priority = secureStorage.getString(key(name, "priority"))?.toIntOrNull() ?: 0
        ProviderConfig(name = name, apiKey = apiKey?.ifEmpty { null }, enabled = enabled, voiceId = voiceId?.ifEmpty { null }, priority = priority)
    }

    suspend fun listSupportedProviders(): List<ProviderConfig> = withContext(Dispatchers.IO) {
        // Return supported providers with their saved config values
        val supported = listOf(
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
        supported.map { name ->
            val c = getConfig(name)
            c
        }
    }
}
