package com.samim.jarvis.voice.providers

import com.samim.jarvis.voice.TtsProvider
import com.samim.jarvis.voice.TtsProviderManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ProviderOrchestrator: attempts synthesis using user's selected provider and falls back automatically.
 * Behavior:
 *  - Query provider names and priorities from VoiceProviderRepository
 *  - For each enabled provider in priority order, attempt to synthesize
 *  - If provider returns non-empty bytes, return them
 *  - If provider returns empty bytes, treat as playback handled
 *  - If provider fails or missing API key, continue to next
 *  - Finally, fallback to AndroidTTS via TtsProviderManager
 */
class ProviderOrchestrator(
    private val ttsProviderManager: TtsProviderManager,
    private val repo: VoiceProviderRepository
) {

    suspend fun synthesizeWithFallback(
        text: String,
        voice: String?,
        speed: Float?,
        pitch: Float?,
        lang: String?,
        meta: Map<String, String>? = null
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            // Determine candidate provider list: use selected provider first, then enabled providers by priority
            val selected = ttsProviderManager.getSelectedProviderName()
            val candidates = mutableListOf<String>()
            if (!selected.isNullOrBlank()) candidates.add(selected)

            val supported = repo.listSupportedNames()
            for (name in supported) {
                if (name == selected) continue
                val enabled = repo.isEnabled(name)
                if (enabled) candidates.add(name)
            }

            // Try candidates
            for (name in candidates) {
                val provider: TtsProvider? = ttsProviderManager.getProviderByName(name)
                if (provider == null) continue
                // If provider requires API key, check
                val apiKey = repo.getApiKey(name)
                if (name != "AndroidTTS" && apiKey.isNullOrBlank()) {
                    // skip providers without API key
                    continue
                }

                val res = provider.synthesize(text, voice, speed, pitch, lang, meta)
                if (res.isSuccess) {
                    val bytes = res.getOrNull()
                    if (bytes != null) {
                        if (bytes.isNotEmpty()) {
                            return@withContext Result.success(bytes)
                        } else {
                            // provider handled playback directly
                            return@withContext Result.success(ByteArray(0))
                        }
                    }
                }
                // else try next
            }

            // Final fallback: AndroidTTS
            val androidProvider = ttsProviderManager.getProviderByName("AndroidTTS")
            if (androidProvider != null) {
                val res = androidProvider.synthesize(text, voice, speed, pitch, lang, meta)
                return@withContext res
            }

            return@withContext Result.failure(RuntimeException("No available TTS provider"))
        } catch (t: Throwable) {
            return@withContext Result.failure(t)
        }
    }
}
