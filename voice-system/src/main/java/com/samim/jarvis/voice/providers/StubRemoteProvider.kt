package com.samim.jarvis.voice.providers

import com.samim.jarvis.voice.TtsProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Generic stub provider which returns failure unless an API key is present in SecureStorage via VoiceProviderRepository.
 * Each provider should be implemented later to perform real network calls.
 */
class StubRemoteProvider(private val nameOverride: String, private val repo: VoiceProviderRepository) : TtsProvider {
    override val name: String = nameOverride

    override suspend fun synthesize(
        text: String,
        voice: String?,
        speed: Float?,
        pitch: Float?,
        lang: String?,
        meta: Map<String, String>?
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        val apiKey = repo.getApiKey(name)
        if (apiKey.isNullOrBlank()) {
            return@withContext Result.failure(RuntimeException("Missing API key for $name"))
        }
        // Placeholder: do not perform network calls until keys and implementations are provided.
        return@withContext Result.failure(RuntimeException("Provider $name not implemented yet"))
    }
}
