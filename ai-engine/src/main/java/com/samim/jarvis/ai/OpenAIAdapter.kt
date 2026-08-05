package com.samim.jarvis.ai

import com.samim.jarvis.api.ApiManager
import com.samim.jarvis.security.SecureStorage

class OpenAIAdapter(private val apiManager: ApiManager, private val secureStorage: SecureStorage) : AIProvider {
    override val name: String = "OpenAI"

    override suspend fun sendMessage(payload: Map<String, Any>): Any {
        val apiKey = secureStorage.getString("openai_api_key")
            ?: throw IllegalStateException("OpenAI API key not configured")
        val service = apiManager.makeOpenAiService("https://api.openai.com/", apiKey)
        // Forward payload to the Retrofit service - the service returns Any as a placeholder
        return service.createChat(payload)
    }
}
