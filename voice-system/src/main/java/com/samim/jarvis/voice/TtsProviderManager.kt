package com.samim.jarvis.voice

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsProviderManager @Inject constructor(private val providers: List<TtsProvider>, private val secureStorage: com.samim.jarvis.security.SecureStorage) {

    fun listProviders(): List<String> = providers.map { it.name }

    fun getSelectedProviderName(): String = secureStorage.getString("tts_provider") ?: providers.firstOrNull()?.name ?: ""

    fun getProviderByName(name: String): TtsProvider? = providers.firstOrNull { it.name == name }

    fun selectProvider(name: String) {
        secureStorage.putString("tts_provider", name)
    }
}
