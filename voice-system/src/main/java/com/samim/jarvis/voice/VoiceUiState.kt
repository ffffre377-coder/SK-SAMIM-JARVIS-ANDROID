package com.samim.jarvis.voice

import androidx.compose.ui.graphics.Color

data class VoiceUiState(
    val listening: Boolean = false,
    val lastTranscript: String = "",
    val femalePreferred: Boolean = true,
    val wakeWordEnabled: Boolean = false,
    val language: String = "en-US",
    val personalityMode: String? = null,
    val avatarState: AvatarState = AvatarState.Idle,
    val themeColor: Color = Color(0xFF00BCD4)
)

enum class AvatarState {
    Idle,
    Listening,
    Thinking,
    Speaking
}
