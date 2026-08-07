package com.samim.jarvis.voice

import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleTtsAdapter(private val secureStorage: SecureStorage) : TtsProvider {
    override val name: String = "GoogleTTS"

    override suspend fun synthesize(
        text: String,
        voice: String?,
        speed: Float?,
        pitch: Float?,
        lang: String?,
        meta: Map<String, String>?
    ): Result<ByteArray> {
        return withContext(Dispatchers.IO) {
            // Scaffold: integrate Google Cloud TTS here using the API key from SecureStorage (google_tts_api_key)
            Result.failure(Exception("Google TTS not implemented - please configure adapter"))
        }
    }
}
