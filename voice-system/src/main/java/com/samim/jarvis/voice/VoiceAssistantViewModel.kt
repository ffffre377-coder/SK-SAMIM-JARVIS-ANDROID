package com.samim.jarvis.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samim.jarvis.ai.ChatRepository
import com.samim.jarvis.security.SecureStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class VoiceUiState(
    val listening: Boolean = false,
    val lastTranscript: String = "",
    val femalePreferred: Boolean = true,
    val wakeWordEnabled: Boolean = false,
    val language: String = "en-US"
)

@HiltViewModel
class VoiceAssistantViewModel @Inject constructor(
    private val sttManager: SpeechToTextManager,
    private val ttsManager: TextToSpeechManager,
    private val secureStorage: SecureStorage,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VoiceUiState())
    val state: StateFlow<VoiceUiState> = _state

    init {
        // load preferences
        val female = secureStorage.getString("voice_female")?.toBoolean() ?: true
        val wake = secureStorage.getString("wake_word_enabled")?.toBoolean() ?: false
        val lang = secureStorage.getString("voice_language") ?: "en-US"
        _state.value = _state.value.copy(femalePreferred = female, wakeWordEnabled = wake, language = lang)

        ttsManager.setPreferredFemale(female)
        ttsManager.setLanguage(Locale.forLanguageTag(lang))

        sttManager.setListener(object : SpeechToTextManager.Listener {
            override fun onPartialResult(text: String) {
                _state.value = _state.value.copy(lastTranscript = text)
            }

            override fun onResult(text: String) {
                _state.value = _state.value.copy(lastTranscript = text, listening = false)
                // send to chat repository as user message (conversationId default 1)
                viewModelScope.launch {
                    chatRepository.sendUserMessage(1, text)
                }
            }

            override fun onError(error: String) {
                _state.value = _state.value.copy(listening = false)
            }
        })
    }

    fun startListening() {
        secureStorage.putString("wake_word_enabled", _state.value.wakeWordEnabled.toString())
        sttManager.startListening(_state.value.language)
        _state.value = _state.value.copy(listening = true)
    }

    fun stopListening() {
        sttManager.stopListening()
        _state.value = _state.value.copy(listening = false)
    }

    fun speak(text: String) {
        ttsManager.speak(text)
    }

    fun setFemalePreferred(pref: Boolean) {
        secureStorage.putString("voice_female", pref.toString())
        _state.value = _state.value.copy(femalePreferred = pref)
        ttsManager.setPreferredFemale(pref)
    }

    fun setWakeWordEnabled(enabled: Boolean) {
        secureStorage.putString("wake_word_enabled", enabled.toString())
        _state.value = _state.value.copy(wakeWordEnabled = enabled)
        // wake-word service wiring handled in later commit
    }

    fun setLanguage(langTag: String) {
        secureStorage.putString("voice_language", langTag)
        _state.value = _state.value.copy(language = langTag)
        ttsManager.setLanguage(Locale.forLanguageTag(langTag))
    }
}
