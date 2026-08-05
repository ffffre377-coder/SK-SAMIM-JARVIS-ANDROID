package com.samim.jarvis.ai

import com.samim.jarvis.api.ApiManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(private val apiManager: ApiManager) {

    suspend fun sendMessageToProvider(provider: String, payload: Map<String, Any>): Any? {
        return when (provider.lowercase()) {
            "openai" -> {
                // default OpenAI base URL - user must configure key and base
                val svc = apiManager.makeOpenAiService("https://api.openai.com/")
                svc.createChat(payload)
            }
            "gemini" -> {
                val svc = apiManager.makeGeminiService("https://api.gemini.example/")
                svc.createMessage(payload)
            }
            else -> null
        }
    }
}
