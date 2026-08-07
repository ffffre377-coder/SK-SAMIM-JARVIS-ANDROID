package com.samim.jarvis.voice.assistants

interface CodingAssistant {
    suspend fun generateTemplate(language: String, name: String): String
    suspend fun explainError(code: String, errorMessage: String): String
    suspend fun saveSnippet(title: String, code: String): String
}
