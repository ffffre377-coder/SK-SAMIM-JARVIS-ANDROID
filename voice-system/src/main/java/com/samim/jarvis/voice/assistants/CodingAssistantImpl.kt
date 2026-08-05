package com.samim.jarvis.voice.assistants

import android.content.Context
import com.samim.jarvis.security.SecureStorage

interface CodingAssistant {
    suspend fun generateTemplate(language: String, name: String): String
    suspend fun explainError(code: String, errorMessage: String): String
    suspend fun saveSnippet(title: String, code: String): String
}

class CodingAssistantImpl(context: Context, secureStorage: SecureStorage) : CodingAssistant {
    private val repo = SecureSnippetRepository(secureStorage)

    override suspend fun generateTemplate(language: String, name: String): String {
        return when (language.lowercase()) {
            "python" -> "# $name.py\ndef main():\n    print(\"Hello from $name\")\n\nif __name__ == '__main__':\n    main()"
            "java" -> "public class $name {\n    public static void main(String[] args) {\n        System.out.println(\"Hello from $name\");\n    }\n}"
            "kotlin" -> "fun main() {\n    println(\"Hello from $name\")\n}"
            "javascript" -> "function main() {\n    console.log('Hello from $name');\n}\nmain();"
            "html" -> "<!doctype html>\n<html><head><meta charset=\"utf-8\"><title>$name</title></head><body><h1>Hello from $name</h1></body></html>"
            else -> "// No template available for $language"
        }
    }

    override suspend fun explainError(code: String, errorMessage: String): String {
        // Simple heuristic explanation — real implementation may use AI
        return "I saw an error: $errorMessage. Check common issues: syntax errors, missing imports, or wrong indentation. If you share the code and the full stack trace I can help more."
    }

    override suspend fun saveSnippet(title: String, code: String): String {
        return repo.saveSnippet(title, code)
    }
}
