*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/assistants/CodingAssistantImpl.kt
@@
-    override suspend fun generateTemplate(language: String, name: String): String {
-        // If AI is available, ask it to generate a short template
-        if (chatRepository != null) {
-            return try {
-                val prompt = "Generate a concise $language project template named $name. Include a main entry point and a short comment explaining usage."
-                val res = chatRepository.sendUserMessage(1, prompt)
-                if (res.isSuccess) {
-                    res.getOrNull()?.toString() ?: localTemplate(language, name)
-                } else localTemplate(language, name)
-            } catch (e: Exception) {
-                localTemplate(language, name)
-            }
-        }
-        return localTemplate(language, name)
-    }
+    override suspend fun generateTemplate(language: String, name: String): String {
+        // Respect AI opt-in setting. If enabled and a ChatRepository is available, use AI generation.
+        val aiEnabled = secureStorage.getString("coding_ai_enabled") == "true"
+        val repoToUse = chatRepository ?: com.samim.jarvis.ai.ChatRepositoryHolder.instance
+        if (aiEnabled && repoToUse != null) {
+            return try {
+                val prompt = "Generate a concise $language project template named $name. Include a main entry point and a short comment explaining usage."
+                val res = repoToUse.sendUserMessage(1, prompt)
+                if (res.isSuccess) {
+                    res.getOrNull()?.toString() ?: localTemplate(language, name)
+                } else localTemplate(language, name)
+            } catch (e: Exception) {
+                localTemplate(language, name)
+            }
+        }
+        return localTemplate(language, name)
+    }
@@
-        if (chatRepository != null) {
-            return try {
-                val prompt = "I have this code:\n```\n$code\n```\nIt produced the following error:\n$errorMessage\nPlease explain the likely causes and show a corrected snippet if possible."
-                val res = chatRepository.sendUserMessage(1, prompt)
-                if (res.isSuccess) res.getOrNull()?.toString() ?: "I saw an error: $errorMessage" else "I saw an error: $errorMessage"
-            } catch (e: Exception) {
-                "I saw an error: $errorMessage"
-            }
-        }
+        val aiEnabled = secureStorage.getString("coding_ai_enabled") == "true"
+        val repoToUse = chatRepository ?: com.samim.jarvis.ai.ChatRepositoryHolder.instance
+        if (aiEnabled && repoToUse != null) {
+            return try {
+                val prompt = "I have this code:\n```\n$code\n```\nIt produced the following error:\n$errorMessage\nPlease explain the likely causes and show a corrected snippet if possible."
+                val res = repoToUse.sendUserMessage(1, prompt)
+                if (res.isSuccess) res.getOrNull()?.toString() ?: "I saw an error: $errorMessage" else "I saw an error: $errorMessage"
+            } catch (e: Exception) {
+                "I saw an error: $errorMessage"
+            }
+        }
         return "I saw an error: $errorMessage. Check common issues: syntax errors, missing imports, or wrong indentation. If you share the code and the full stack trace I can help more."
     }
*** End Patch
