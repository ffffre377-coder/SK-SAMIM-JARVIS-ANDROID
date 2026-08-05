package com.samim.jarvis.ui.settings

data class ProviderSetting(
    val id: String,
    val displayName: String,
    var apiKey: String = "",
    var visible: Boolean = false,
    var enabled: Boolean = true,
    var status: ProviderStatus = ProviderStatus.Idle
)

sealed class ProviderStatus {
    object Idle : ProviderStatus()
    object Testing : ProviderStatus()
    data class Success(val message: String) : ProviderStatus()
    data class Failure(val message: String) : ProviderStatus()
}
