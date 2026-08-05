package com.samim.jarvis.voice.providers

import com.samim.jarvis.voice.TtsProvider
import com.samim.jarvis.voice.TextToSpeechManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidTtsProvider(private val ttsManager: TextToSpeechManager) : TtsProvider {
    override val name: String = "AndroidTTS"

    override suspend fun synthesize(
        text: String,
        voice: String?,
        speed: Float?,
        pitch: Float?,
        lang: String?
    ): Result<ByteArray> {
        // Use TextToSpeechManager to speak directly as an offline fallback.
        // Return an empty byte array to signal the caller that Android TTS handled playback.
        return try {
            withContext(Dispatchers.Main) {
                if (!voice.isNullOrBlank()) {
                    // Note: voice selection for AndroidTTS may be handled by TextToSpeechManager internals.
                }
                // Configure speed/pitch if provided
                speed?.let { ttsManager.setSpeechRate(it) }
                pitch?.let { ttsManager.setPitch(it) }
                lang?.let { ttsManager.setLanguage(java.util.Locale.forLanguageTag(it)) }
                ttsManager.speak(text)
            }
            Result.success(ByteArray(0))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
