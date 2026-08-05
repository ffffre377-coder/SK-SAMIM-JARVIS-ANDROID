*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/VoiceAssistantViewModel.kt
@@
-    private val phoneCommandProcessor by lazy { com.samim.jarvis.phone.PhoneCommandProcessor(context) }
+    private val phoneCommandProcessor by lazy { com.samim.jarvis.phone.PhoneCommandProcessor(context) }
+    // provider orchestrator & engines
+    private val voiceProviderRepo by lazy { com.samim.jarvis.voice.providers.VoiceProviderRepository(secureStorage) }
+    private val providerOrchestrator by lazy { com.samim.jarvis.voice.providers.ProviderOrchestrator(ttsProviderManager, voiceProviderRepo) }
+    private val personalityEngine by lazy { com.samim.jarvis.voice.personality.DefaultPersonalityEngine() }
+    private val emotionEngine by lazy { com.samim.jarvis.voice.emotion.DefaultEmotionEngine() }
@@
-    private suspend fun speakByProvider(text: String) {
-        // choose provider
-        val providerName = ttsProviderManager.getSelectedProviderName()
-        val provider = ttsProviderManager.getProviderByName(providerName)
-        val voiceId = secureStorage.getString("tts_voice")
-        val speed = secureStorage.getString("tts_speed")?.toFloatOrNull()
-        val pitch = secureStorage.getString("tts_pitch")?.toFloatOrNull()
-
-        if (provider != null) {
-            val res = withContext(Dispatchers.IO) { provider.synthesize(text, voiceId, speed, pitch, _state.value.language) }
-            if (res.isSuccess) {
-                val bytes = res.getOrNull()
-                // Interpret an empty byte array as "provider handled playback (e.g., Android TTS)".
-                if (bytes != null) {
-                    if (bytes.isNotEmpty()) {
-                        // play audio bytes
-                        ttsPlayback.play(bytes)
-                        return
-                    } else {
-                        // provider handled playback directly (no bytes) - nothing to do
-                        return
-                    }
-                }
-            }
-        }
-
-        // fallback to Android TTS
-        ttsManager.speak(text)
-    }
+    private suspend fun speakByProvider(text: String) {
+        // Build metadata from personality & emotion engines
+        val meta = mutableMapOf<String, String>()
+        meta.putAll(personalityEngine.getMetadata())
+        meta.putAll(emotionEngine.getMetadata())
+
+        val voiceId = secureStorage.getString("tts_voice")
+        val speed = secureStorage.getString("tts_speed")?.toFloatOrNull()
+        val pitch = secureStorage.getString("tts_pitch")?.toFloatOrNull()
+
+        val res = providerOrchestrator.synthesizeWithFallback(text, voiceId, speed, pitch, _state.value.language, meta)
+        if (res.isSuccess) {
+            val bytes = res.getOrNull()
+            if (bytes != null && bytes.isNotEmpty()) {
+                // play audio bytes returned by provider
+                ttsPlayback.play(bytes)
+            }
+            // else empty bytes -> provider handled playback (e.g., AndroidTTS)
+            return
+        }
+
+        // fallback to local Android TTS manager as a last resort
+        ttsManager.speak(text)
+    }
*** End Patch
