*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/VoiceAssistantViewModel.kt
@@
     private val personalityEngine by lazy { com.samim.jarvis.voice.personality.DefaultPersonalityEngine() }
     private val emotionEngine by lazy { com.samim.jarvis.voice.emotion.DefaultEmotionEngine() }
+    // Memory repository
+    private val memoryRepo by lazy { com.samim.jarvis.memory.EncryptedMemoryRepository(secureStorage) }
@@
     init {
         // load preferences
@@
         sttManager.setListener(object : SpeechToTextManager.Listener {
@@
             override fun onResult(text: String) {
                 _state.value = _state.value.copy(lastTranscript = text, listening = false)
                 lastTranscriptText = text
 
                 // Route through command parsing and phone-control first, with AI fallback
                 viewModelScope.launch {
+                    // Load memory-derived context (e.g., nickname) to include in AI fallback
+                    val nickname = memoryRepo.getString("nickname")
+                    val memoryContext = mutableMapOf<String, String>()
+                    if (!nickname.isNullOrBlank()) memoryContext["nickname"] = nickname
+
                     val locale = Locale.forLanguageTag(_state.value.language)
                     val parsed = com.samim.jarvis.voice.command.CommandParser.parse(text, locale)
@@
                     if (handledLocally) {
@@
                         )
                     } else {
                         // Not a local command — send to AI
-                        callAiAndSpeak(text)
+                        // Inject memory context into AI prompt via chatRepository (if supported)
+                        val finalText = if (memoryContext.isNotEmpty()) "[context=${memoryContext}]
+$text" else text
+                        callAiAndSpeak(finalText)
                     }
                 }
             }
*** End Patch
