package com.samim.jarvis.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samim.jarvis.api.ApiManager
import com.samim.jarvis.ai.AIProviderManager
import com.samim.jarvis.security.SecureStorage
import com.samim.jarvis.voice.TtsProviderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val secureStorage: SecureStorage,
    private val apiManager: ApiManager,
    private val aiProviderManager: AIProviderManager,
    private val ttsProviderManager: TtsProviderManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val providerDefs = listOf(
        ProviderSetting(id = "openai", displayName = "OpenAI"),
        ProviderSetting(id = "gemini", displayName = "Gemini"),
        ProviderSetting(id = "claude", displayName = "Claude"),
        ProviderSetting(id = "deepseek", displayName = "DeepSeek"),
        ProviderSetting(id = "groq", displayName = "Groq"),
        ProviderSetting(id = "openrouter", displayName = "OpenRouter")
    )

    init {
        load()
    }

    private fun load() {
        val list = providerDefs.map { def ->
            val key = getApiKeyStorageKey(def.id)
            val enabledKey = getEnabledStorageKey(def.id)
            def.copy(
                apiKey = secureStorage.getString(key) ?: "",
                enabled = secureStorage.getString(enabledKey)?.toBoolean() ?: true
            )
        }
        _state.value = SettingsState(list)
        // set enabled providers in provider manager
        aiProviderManager.setEnabledProviders(list.filter { it.enabled }.map { it.id.capitalizeName() }.toSet())
    }

    fun updateApiKey(id: String, value: String) {
        val list = _state.value.providers.map { if (it.id == id) it.copy(apiKey = value) else it }
        _state.value = SettingsState(list)
    }

    fun toggleVisibility(id: String) {
        val list = _state.value.providers.map { if (it.id == id) it.copy(visible = !it.visible) else it }
        _state.value = SettingsState(list)
    }

    fun saveApiKey(id: String) {
        val provider = _state.value.providers.first { it.id == id }
        val key = getApiKeyStorageKey(id)
        secureStorage.putString(key, provider.apiKey)
    }

    fun setProviderEnabled(id: String, enabled: Boolean) {
        val list = _state.value.providers.map { if (it.id == id) it.copy(enabled = enabled) else it }
        _state.value = SettingsState(list)
        val enabledKey = getEnabledStorageKey(id)
        secureStorage.putString(enabledKey, enabled.toString())
        // update AI provider manager
        aiProviderManager.enableProvider(id.capitalizeName(), enabled)
    }

    fun testProvider(id: String) {
        viewModelScope.launch {
            val list = _state.value.providers
            val provider = list.first { it.id == id }
            // update status to testing
            updateStatus(id, ProviderStatus.Testing)

            val (baseUrl, path) = when (id) {
                "openai" -> Pair("https://api.openai.com/", "v1/models")
                "gemini" -> Pair("https://api.gemini.google.com/", "v1/models")
                "claude" -> Pair("https://api.anthropic.com/", "v1/models")
                "deepseek" -> Pair("https://api.deepseek.ai/", "v1/models")
                "groq" -> Pair("https://api.groq.com/", "v1/models")
                "openrouter" -> Pair("https://api.openrouter.ai/", "v1/models")
                else -> Pair("https://api.openai.com/", "v1/models")
            }

            val result = apiManager.testConnection(baseUrl, provider.apiKey.ifBlank { null }, path)
            if (result.isSuccess) {
                updateStatus(id, ProviderStatus.Success(result.getOrNull() ?: "OK"))
            } else {
                updateStatus(id, ProviderStatus.Failure(result.exceptionOrNull()?.message ?: "Unknown error"))
            }
        }
    }

    private fun updateStatus(id: String, status: ProviderStatus) {
        val list = _state.value.providers.map { if (it.id == id) it.copy(status = status) else it }
        _state.value = SettingsState(list)
    }

    private fun getApiKeyStorageKey(id: String) = "${id}_api_key"
    private fun getEnabledStorageKey(id: String) = "${id}_enabled"

    // TTS settings
    fun saveTtsProvider(provider: String) {
        secureStorage.putString("tts_provider", provider)
    }

    fun saveTtsVoice(voice: String) {
        secureStorage.putString("tts_voice", voice)
    }

    fun saveTtsSpeed(speed: Float) {
        secureStorage.putString("tts_speed", speed.toString())
    }

    fun saveTtsPitch(pitch: Float) {
        secureStorage.putString("tts_pitch", pitch.toString())
    }

    fun testTts(providerName: String, voiceId: String, speed: Float, pitch: Float) {
        viewModelScope.launch {
            val provider = ttsProviderManager.getProviderByName(providerName)
            if (provider == null) {
                // no provider
                return@launch
            }
            val res = provider.synthesize("This is a test of the selected voice.", voiceId, speed, pitch, "en-US")
            if (res.isSuccess) {
                // play audio via TtsPlayback
                // We can't access TtsPlayback here; instead we save to cache and let VoiceAssistant handle playback; for now, nothing
            }
        }
    }
}

private fun String.capitalizeName(): String = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
