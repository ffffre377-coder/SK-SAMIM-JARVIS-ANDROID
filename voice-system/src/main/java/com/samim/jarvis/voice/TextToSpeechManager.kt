package com.samim.jarvis.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    fun speak(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_tts")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
