*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/VoiceAssistantViewModel.kt
@@
     private suspend fun speakByProvider(text: String) {
         // choose provider
         val providerName = ttsProviderManager.getSelectedProviderName()
         val provider = ttsProviderManager.getProviderByName(providerName)
         val voiceId = secureStorage.getString("tts_voice")
         val speed = secureStorage.getString("tts_speed")?.toFloatOrNull()
         val pitch = secureStorage.getString("tts_pitch")?.toFloatOrNull()
 
         if (provider != null) {
             val res = provider.synthesize(text, voiceId, speed, pitch, _state.value.language)
             if (res.isSuccess) {
                 val bytes = res.getOrNull()
-                if (!bytes.isNullOrEmpty()) {
-                    // play audio bytes
-                    ttsPlayback.play(bytes)
-                    return
-                }
+                // Interpret an empty byte array as "provider handled playback (e.g., Android TTS)".
+                if (bytes != null) {
+                    if (bytes.isNotEmpty()) {
+                        // play audio bytes
+                        ttsPlayback.play(bytes)
+                        return
+                    } else {
+                        // provider handled playback directly (no bytes) - nothing to do
+                        return
+                    }
+                }
             }
         }
 
         // fallback to Android TTS
         ttsManager.speak(text)
     }
*** End Patch
