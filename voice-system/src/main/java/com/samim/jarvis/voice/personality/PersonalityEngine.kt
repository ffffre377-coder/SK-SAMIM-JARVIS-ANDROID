package com.samim.jarvis.voice.personality

/**
 * Personality engine: provides a current personality profile that can be used by chat prompts and TTS meta.
 */
interface PersonalityEngine {
    fun getMode(): PersonalityMode
    fun setMode(mode: PersonalityMode)
    fun getMetadata(): Map<String, String>
}

enum class PersonalityMode {
    Friendly,
    Professional,
    Calm,
    Funny
}

class DefaultPersonalityEngine : PersonalityEngine {
    private var mode: PersonalityMode = PersonalityMode.Friendly

    override fun getMode(): PersonalityMode = mode

    override fun setMode(mode: PersonalityMode) {
        this.mode = mode
    }

    override fun getMetadata(): Map<String, String> = mapOf("personality" to mode.name)
}
