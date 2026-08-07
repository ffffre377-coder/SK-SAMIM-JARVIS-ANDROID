package com.samim.jarvis.voice

/**
 * Visual/interaction state driving the JarvisAvatar and waveform composables.
 */
enum class AvatarState {
    Idle,
    Listening,
    Thinking,
    Speaking
}

/**
 * UI state exposed by [VoiceAssistantViewModel] to the voice assistant screens.
 */
data class VoiceUiState(
    val listening: Boolean = false,
    val avatarState: AvatarState = AvatarState.Idle,
    val wakeWordEnabled: Boolean = false,
    val personalityMode: String? = null,
    val lastTranscript: String = "",
    val lastResponse: String = "",
    val statusMessage: String = ""
)
