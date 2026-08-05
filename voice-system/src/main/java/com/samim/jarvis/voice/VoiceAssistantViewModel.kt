package com.samim.jarvis.voice

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samim.jarvis.ai.ChatRepository
import com.samim.jarvis.security.SecureStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VoiceAssistantViewModel @Inject constructor(
    private val sttManager: SpeechToTextManager,
    private val ttsManager: TextToSpeechManager,
    private val secureStorage: SecureStorage,
    private val chatRepository: ChatRepository,
    private val ttsProviderManager: TtsProviderManager,
    private val context: Context,
    private val ttsPlayback: TtsPlayback
) : ViewModel() {

    private val _state = MutableStateFlow(VoiceUiState())
    val state: StateFlow<VoiceUiState> = _state

    init {
        // load preferences
        val female = secureStorage.getString("voice_female")?.toBoolean() ?: true
        val wake = secureStorage.getString("wake_word_enabled")?.toBoolean() ?: false
        val lang = secureStorage.getString("voice_language") ?: "en-US"
        val provider = secureStorage.getString("tts_provider") ?: "ElevenLabs"
        val voiceId = secureStorage.getString("tts_voice") ?: ""
        val speed = secureStorage.getString("tts_speed")?.toFloatOrNull() ?: 1.0f
        val pitch = secureStorage.getString("tts_pitch")?.toFloatOrNull() ?: 1.0f

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
                    val result = chatRepository.sendUserMessage(1, text)
                    if (result.isSuccess) {
                        val resp = result.getOrNull()
                        val respText = resp?.toString() ?: ""
                        // Speak the response using selected TTS provider
                        speakByProvider(respText)
                    }
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
    }

    fun setLanguage(langTag: String) {
        secureStorage.putString("voice_language", langTag)
        _state.value = _state.value.copy(language = langTag)
        ttsManager.setLanguage(Locale.forLanguageTag(langTag))
    }

    fun setTtsProvider(providerName: String) {
        secureStorage.putString("tts_provider", providerName)
    }

    fun setTtsVoice(voiceId: String) {
        secureStorage.putString("tts_voice", voiceId)
    }

    fun setTtsSpeed(speed: Float) {
        secureStorage.putString("tts_speed", speed.toString())
    }

    fun setTtsPitch(pitch: Float) {
        secureStorage.putString("tts_pitch", pitch.toString())
    }

    private suspend fun speakByProvider(text: String) {
        // choose provider
        val providerName = ttsProviderManager.getSelectedProviderName()
        val provider = ttsProviderManager.getProviderByName(providerName)
        val voiceId = secureStorage.getString("tts_voice")
        val speed = secureStorage.getString("tts_speed")?.toFloatOrNull()
        val pitch = secureStorage.getString("tts_pitch")?.toFloatOrNull()

        if (provider != null) {
            val res = provider.synthesize(text, voiceId, speed, pitch, _state.value.language)
            if (res.isSuccess) {
                val bytes = res.getOrNull()
                if (!bytes.isNullOrEmpty()) {
                    // play audio bytes
                    ttsPlayback.play(bytes)
                    return
                }
            }
        }

        // fallback to Android TTS
        ttsManager.speak(text)
    }
}
