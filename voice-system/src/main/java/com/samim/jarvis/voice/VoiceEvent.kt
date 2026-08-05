package com.samim.jarvis.voice

sealed class VoiceEvent {
    data class ConfirmAction(val title: String, val message: String) : VoiceEvent()
    data class Info(val message: String) : VoiceEvent()
}
