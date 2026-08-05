package com.samim.jarvis.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

// Placeholder service interfaces. Implement provider-specific endpoints.
interface OpenAIService {
    @POST("/v1/chat/completions")
    suspend fun createChat(@Body body: Map<String, Any>): Any
}

interface GeminiService {
    @POST("/v1/gemini/messages")
    suspend fun createMessage(@Body body: Map<String, Any>): Any
}
