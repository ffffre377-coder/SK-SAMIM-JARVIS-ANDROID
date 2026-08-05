package com.samim.jarvis.ai

import com.samim.jarvis.api.ApiManager
import com.samim.jarvis.security.SecureStorage

class GeminiAdapter(private val apiManager: ApiManager, private val secureStorage: SecureStorage) : AIProvider {
    override val name: String = "Gemini"

    override suspend fun sendMessage(payload: Map<String, Any>): Any {
        val apiKey = secureStorage.getString("gemini_api_key")
            ?: throw IllegalStateException("Gemini API key not configured")
        val service = apiManager.makeGeminiService("https://api.gemini.google.com/", apiKey)
        return service.createMessage(payload)
    }
}
