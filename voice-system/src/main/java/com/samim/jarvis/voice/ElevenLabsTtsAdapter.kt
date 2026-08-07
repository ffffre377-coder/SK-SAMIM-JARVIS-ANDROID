package com.samim.jarvis.voice

import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ElevenLabsTtsAdapter(private val secureStorage: SecureStorage) : TtsProvider {
    override val name: String = "ElevenLabs"
    private val client = OkHttpClient()

    override suspend fun synthesize(
        text: String,
        voice: String?,
        speed: Float?,
        pitch: Float?,
        lang: String?,
        meta: Map<String, String>?
    ): Result<ByteArray> {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = secureStorage.getString("elevenlabs_api_key")
                    ?: return@withContext Result.failure(Exception("ElevenLabs API key not configured"))

                // Build request body according to ElevenLabs API - simplified
                val payload = JSONObject()
                payload.put("text", text)
                // optional voice settings
                val voiceSettings = JSONObject()
                if (speed != null) voiceSettings.put("voice_speed", speed)
                if (pitch != null) voiceSettings.put("voice_pitch", pitch)
                payload.put("voice_settings", voiceSettings)

                val body: RequestBody = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

                val urlVoice = voice ?: "standard"
                val url = "https://api.elevenlabs.io/v1/text-to-speech/$urlVoice"

                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Accept", "audio/mpeg")
                    .addHeader("xi-api-key", apiKey)
                    .build()

                client.newCall(request).execute().use { resp ->
                    if (!resp.isSuccessful) return@withContext Result.failure(Exception("HTTP ${resp.code}: ${resp.message}"))
                    val bytes = resp.body?.bytes() ?: ByteArray(0)
                    return@withContext Result.success(bytes)
                }
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
    }
}
