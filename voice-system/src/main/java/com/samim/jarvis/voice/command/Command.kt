package com.samim.jarvis.voice.command

sealed class Command {
    data class LaunchApp(val appName: String) : Command()
    data class OpenYouTube(val query: String?) : Command()
    data class CallContact(val contactName: String?) : Command()
    data class SendMessage(val contactName: String?, val message: String?) : Command()
    object ShareFile : Command()
    object OpenCamera : Command()
    data class ToggleFlashlight(val turnOn: Boolean?) : Command()
    data class SetVolume(val level: Int?, val direction: String? = null) : Command()
    object Unknown : Command()
}
