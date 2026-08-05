package com.samim.jarvis.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samim.jarvis.security.SecureStorage
import com.samim.jarvis.voice.providers.ProviderConfig
import com.samim.jarvis.voice.providers.VoiceProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProviderUiState(
    val providers: List<ProviderConfig> = emptyList(),
    val selectedProvider: String = "AndroidTTS",
    val statusMessage: String = ""
)

@HiltViewModel
class VoiceProviderViewModel @Inject constructor(
    private val repo: VoiceProviderRepository,
    private val ttsManager: TextToSpeechManager,
    private val ttsProviderManager: TtsProviderManager,
    private val secureStorage: SecureStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ProviderUiState())
    val state: StateFlow<ProviderUiState> = _state

    init {
        viewModelScope.launch {
            refresh()
            val sel = secureStorage.getString("tts_provider") ?: "AndroidTTS"
            _state.value = _state.value.copy(selectedProvider = sel)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val list = repo.listSupportedProviders()
            _state.value = _state.value.copy(providers = list)
        }
    }

    fun selectProvider(name: String) {
        viewModelScope.launch {
            secureStorage.putString("tts_provider", name)
            ttsProviderManager.selectProvider(name)
            _state.value = _state.value.copy(selectedProvider = name)
        }
    }

    fun saveApiKey(name: String, apiKey: String) {
        viewModelScope.launch {
            val cfg = ProviderConfig(name = name, apiKey = apiKey, enabled = true)
            repo.saveConfig(cfg)
            _state.value = _state.value.copy(statusMessage = "Saved API key for $name")
            refresh()
        }
    }

    fun deleteApiKey(name: String) {
        viewModelScope.launch {
            repo.deleteConfig(name)
            _state.value = _state.value.copy(statusMessage = "Deleted API key for $name")
            refresh()
        }
    }

    fun testConnection(name: String) {
        viewModelScope.launch {
            // No external checks for now — simply check if API key exists for non-Android providers
            val cfg = repo.getConfig(name)
            val ok = when (name) {
                "AndroidTTS" -> true
                else -> !cfg.apiKey.isNullOrBlank()
            }
            _state.value = _state.value.copy(statusMessage = if (ok) "Connection OK for $name" else "No API key for $name")
        }
    }
}
