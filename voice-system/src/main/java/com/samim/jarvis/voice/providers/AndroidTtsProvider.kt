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
        lang: String?,
        meta: Map<String, String>?
    ): Result<ByteArray> {
        return try {
            withContext(Dispatchers.Main) {
                speed?.let { ttsManager.setSpeechRate(it) }
                pitch?.let { ttsManager.setPitch(it) }
                lang?.let { ttsManager.setLanguage(java.util.Locale.forLanguageTag(it)) }
                // Personality/emotion hints may be in meta but Android TTS will only use rate/pitch for now
                ttsManager.speak(text)
            }
            // empty bytes indicate playback handled directly
            Result.success(ByteArray(0))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
