package com.samim.jarvis.ai

interface AIProvider {
    val name: String
    suspend fun sendMessage(payload: Map<String, Any>): Any
}
