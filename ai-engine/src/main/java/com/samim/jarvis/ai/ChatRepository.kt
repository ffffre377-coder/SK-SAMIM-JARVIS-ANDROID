*** Begin Patch
*** Update File: ai-engine/src/main/java/com/samim/jarvis/ai/ChatRepository.kt
@@
-    suspend fun sendUserMessage(conversationId: Long, messageText: String): Result<Any> {
+    suspend fun sendUserMessage(conversationId: Long, messageText: String, personalityMode: String? = null): Result<Any> {
         return try {
@@
-            // prepare payload (simple form)
-            val payload = mapOf("messages" to listOf(mapOf("role" to "user", "content" to messageText)))
-
-            val response = withContext(Dispatchers.IO) { providerManager.sendWithFallback(payload) }
+            // prepare payload (add personality system prompt if provided)
+            val messages = mutableListOf<Map<String, String>>()
+            if (!personalityMode.isNullOrBlank()) {
+                val systemPrompt = when (personalityMode) {
+                    "Friendly" -> "You are a helpful and friendly assistant. Speak like a helpful friend."
+                    "Funny" -> "You are a witty assistant who adds light humor to responses. Be polite and humorous."
+                    "Emotional" -> "You are empathetic and caring. Understand feelings and reply in a supportive way."
+                    "Professional" -> "You are professional and concise. Provide expert and serious answers."
+                    "JARVIS" -> "You are a futuristic AI assistant — precise, smart, and slightly formal."
+                    "CasualFriend" -> "You speak in a friendly Hinglish tone: mix Hindi and English casually when appropriate."
+                    "Motivational" -> "You are encouraging and motivational — give positive and uplifting responses."
+                    else -> "You are a helpful assistant."
+                }
+                messages.add(mapOf("role" to "system", "content" to systemPrompt))
+            }
+            messages.add(mapOf("role" to "user", "content" to messageText))
+
+            val payload = mapOf("messages" to messages)
+
+            val response = withContext(Dispatchers.IO) { providerManager.sendWithFallback(payload) }
*** End Patch
