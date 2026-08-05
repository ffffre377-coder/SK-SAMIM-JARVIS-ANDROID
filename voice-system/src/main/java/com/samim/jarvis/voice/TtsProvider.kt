package com.samim.jarvis.voice

interface TtsProvider {
    val name: String
    suspend fun synthesize(
        text: String,
        voice: String? = null,
        speed: Float? = null,
        pitch: Float? = null,
        lang: String? = null
    ): Result<ByteArray>
}
