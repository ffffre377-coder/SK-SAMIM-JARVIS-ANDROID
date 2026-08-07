package com.samim.jarvis.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var initialized = false
    private var preferredFemale = false
    private var preferredLanguage: Locale = Locale.ENGLISH

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true
            tts?.language = preferredLanguage
        }
    }

    fun setPreferredFemale(female: Boolean) {
        preferredFemale = female
    }

    fun setLanguage(locale: Locale) {
        preferredLanguage = locale
        if (initialized) tts?.language = locale
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    fun setPitch(pitch: Float) {
        tts?.setPitch(pitch)
    }

    fun speak(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            // attempt to choose a female voice if requested
            if (preferredFemale) {
                val voice = findFemaleVoice(preferredLanguage)
                voice?.let { tts?.voice = it }
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_tts")
        }
    }

    private fun findFemaleVoice(locale: Locale): Voice? {
        val voices = tts?.voices ?: return null
        // heuristics: prefer matching locale and name contains female-related tokens
        val candidates = voices.filter { it.locale.language == locale.language }
        val femaleKeywords = listOf("female", "f", "woman", "voice_f", "she")
        val found = candidates.firstOrNull { v ->
            val name = v.name.lowercase(Locale.getDefault())
            femaleKeywords.any { name.contains(it) }
        }
        return found ?: candidates.firstOrNull()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        initialized = false
    }
}
