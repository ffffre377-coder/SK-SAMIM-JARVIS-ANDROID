package com.samim.jarvis.ui.voice

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VoiceAssistantViewModel @Inject constructor() : ViewModel() {
    fun startListening() {
        // TODO: delegate to SpeechToTextManager
    }

    fun stopListening() {
        // TODO: delegate to SpeechToTextManager
    }
}
