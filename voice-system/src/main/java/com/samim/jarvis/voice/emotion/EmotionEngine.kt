package com.samim.jarvis.voice.emotion

/**
 * EmotionEngine provides current emotion/style settings for TTS and conversation.
 */
interface EmotionEngine {
    fun getEmotionLevel(): Int // 0..100
    fun setEmotionLevel(level: Int)
    fun getMetadata(): Map<String, String>
}

class DefaultEmotionEngine : EmotionEngine {
    private var level: Int = 50
    override fun getEmotionLevel(): Int = level
    override fun setEmotionLevel(level: Int) {
        this.level = level.coerceIn(0, 100)
    }

    override fun getMetadata(): Map<String, String> = mapOf("emotionLevel" to level.toString())
}
