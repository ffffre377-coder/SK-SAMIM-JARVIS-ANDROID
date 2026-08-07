package com.samim.jarvis.voice.assistants

import com.samim.jarvis.ai.ChatRepository
import com.samim.jarvis.ai.ChatRepositoryHolder
import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * CodingAssistant implementation with optional AI integration.
 * If a ChatRepository is provided, code generation and error explanation will call the AI.
 * Otherwise, it falls back to local templates and heuristics.
 */
class CodingAssistantImpl(
    private val contextArg: android.content.Context,
    private val secureStorage: SecureStorage,
    private val chatRepository: ChatRepository? = null
) : CodingAssistant {

    private val repo = SecureSnippetRepository(secureStorage)

    override suspend fun generateTemplate(language: String, name: String): String {
        // Respect AI opt-in setting. If enabled and a ChatRepository is available, use AI generation.
        val aiEnabled = secureStorage.getString("coding_ai_enabled") == "true"
        val repoToUse = chatRepository ?: ChatRepositoryHolder.instance
        if (aiEnabled && repoToUse != null) {
            return try {
                val prompt = "Generate a concise $language project template named $name. Include a main entry point and a short comment explaining usage."
                val res = repoToUse.sendUserMessage(1, prompt)
                if (res.isSuccess) {
                    res.getOrNull()?.toString() ?: localTemplate(language, name)
                } else localTemplate(language, name)
            } catch (e: Exception) {
                localTemplate(language, name)
            }
        }
        return localTemplate(language, name)
    }

    private fun localTemplate(language: String, name: String): String = when (language.lowercase()) {
        "python" -> "# $name.py\ndef main():\n    print(\"Hello from $name\")\nif __name__ == '__main__':\n    main()"
        "java" -> "public class $name {\n    public static void main(String[] args) {\n        System.out.println(\"Hello from $name\");\n    }\n}"
        "kotlin" -> "fun main() {\n    println(\"Hello from $name\")\n}"
        "javascript" -> "function main() {\n    console.log('Hello from $name');\n}\nmain();"
        "html" -> "<!doctype html>\n<html><head><meta charset=\"utf-8\"><title>$name</title></head><body><h1>Hello from $name</h1></body></html>"
        else -> "// No template available for $language"
    }

    override suspend fun explainError(code: String, errorMessage: String): String {
        val aiEnabled = secureStorage.getString("coding_ai_enabled") == "true"
        val repoToUse = chatRepository ?: ChatRepositoryHolder.instance
        if (aiEnabled && repoToUse != null) {
            return try {
                val prompt = "I have this code:\n```\n$code\n```\nIt produced the following error:\n$errorMessage\nPlease explain the likely causes and show a corrected snippet if possible."
                val res = repoToUse.sendUserMessage(1, prompt)
                if (res.isSuccess) res.getOrNull()?.toString() ?: "I saw an error: $errorMessage" else "I saw an error: $errorMessage"
            } catch (e: Exception) {
                "I saw an error: $errorMessage"
            }
        }
        return "I saw an error: $errorMessage. Check common issues: syntax errors, missing imports, or wrong indentation. If you share the code and the full stack trace I can help more."
    }

    override suspend fun saveSnippet(title: String, code: String): String {
        return repo.saveSnippet(title, code)
    }
}
