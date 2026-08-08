package com.samim.jarvis.ui.settings

data class SettingsState(
    val providers: List<ProviderSetting> = emptyList()
)

sealed class TtsTestState {
    object Idle : TtsTestState()
    object Testing : TtsTestState()
    data class Success(val message: String) : TtsTestState()
    data class Failure(val message: String) : TtsTestState()
}
