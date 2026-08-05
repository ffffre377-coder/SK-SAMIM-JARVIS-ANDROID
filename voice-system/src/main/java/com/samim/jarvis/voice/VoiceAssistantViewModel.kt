*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/VoiceAssistantViewModel.kt
@@
     private val personalityEngine by lazy { com.samim.jarvis.voice.personality.DefaultPersonalityEngine() }
     private val emotionEngine by lazy { com.samim.jarvis.voice.emotion.DefaultEmotionEngine() }
     // Memory repository
     private val memoryRepo by lazy { com.samim.jarvis.memory.EncryptedMemoryRepository(secureStorage) }
+    // load persisted personality mode
+    init {
+        val saved = secureStorage.getString("personality_mode")
+        if (!saved.isNullOrBlank()) {
+            try {
+                val mode = com.samim.jarvis.voice.personality.PersonalityMode.valueOf(saved)
+                personalityEngine.setMode(mode)
+            } catch (e: Exception) {
+                // ignore invalid stored value
+            }
+        }
+    }
@@
-    fun setFemalePreferred(pref: Boolean) {
+    fun setFemalePreferred(pref: Boolean) {
         secureStorage.putString("voice_female", pref.toString())
         _state.value = _state.value.copy(femalePreferred = pref)
         ttsManager.setPreferredFemale(pref)
     }
+
+    fun setPersonality(mode: com.samim.jarvis.voice.personality.PersonalityMode) {
+        personalityEngine.setMode(mode)
+        secureStorage.putString("personality_mode", mode.name)
+        // update UI state if desired
+        _state.value = _state.value.copy(personalityMode = mode.name)
+    }
*** End Patch
