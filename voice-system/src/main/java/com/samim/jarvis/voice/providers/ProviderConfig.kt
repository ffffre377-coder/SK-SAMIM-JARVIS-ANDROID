package com.samim.jarvis.voice.providers

import kotlinx.serialization.Serializable

@Serializable
data class ProviderConfig(
    val name: String,
    val apiKey: String? = null,
    val enabled: Boolean = false,
    val voiceId: String? = null,
    val priority: Int = 0
)
