*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/VoiceAssistantViewModel.kt
@@
     private val _state = MutableStateFlow(VoiceUiState())
     val state: StateFlow<VoiceUiState> = _state
@@
     init {
@@
         sttManager.setListener(object : SpeechToTextManager.Listener {
@@
             override fun onResult(text: String) {
                 _state.value = _state.value.copy(lastTranscript = text, listening = false)
                 lastTranscriptText = text
@@
                     if (handledLocally) {
@@
                     } else {
                         // Not a local command — send to AI
                         // Inject memory context into AI prompt via chatRepository (if supported)
                         val finalText = if (memoryContext.isNotEmpty()) "[context=${memoryContext}]\n$text" else text
-                        callAiAndSpeak(finalText)
+                        // Indicate thinking
+                        _state.value = _state.value.copy(avatarState = AvatarState.Thinking)
+                        callAiAndSpeak(finalText)
                     }
                 }
             }
@@
     fun startListening() {
         secureStorage.putString("wake_word_enabled", _state.value.wakeWordEnabled.toString())
         sttManager.startListening(_state.value.language)
-        _state.value = _state.value.copy(listening = true)
+        _state.value = _state.value.copy(listening = true, avatarState = AvatarState.Listening)
     }
@@
     fun stopListening() {
         sttManager.stopListening()
-        _state.value = _state.value.copy(listening = false)
+        _state.value = _state.value.copy(listening = false, avatarState = AvatarState.Idle)
     }
@@
     private suspend fun speakByProvider(text: String) {
@@
-        val res = providerOrchestrator.synthesizeWithFallback(text, voiceId, speed, pitch, _state.value.language, meta)
+        val res = providerOrchestrator.synthesizeWithFallback(text, voiceId, speed, pitch, _state.value.language, meta)
         if (res.isSuccess) {
             val bytes = res.getOrNull()
             if (bytes != null && bytes.isNotEmpty()) {
-                // play audio bytes returned by provider
-                ttsPlayback.play(bytes)
+                // play audio bytes returned by provider; set avatar speaking
+                // Register listener to drive avatar mouth animation
+                ttsPlayback.setListener(object : TtsPlayback.Listener {
+                    override fun onStart() {
+                        _state.value = _state.value.copy(avatarState = AvatarState.Speaking)
+                    }
+
+                    override fun onProgress(level: Float) {
+                        _state.value = _state.value.copy(mouthLevel = level)
+                    }
+
+                    override fun onComplete() {
+                        _state.value = _state.value.copy(avatarState = AvatarState.Idle, mouthLevel = 0f)
+                    }
+                })
+                ttsPlayback.play(bytes)
                 return
             }
             // else empty bytes -> provider handled playback (e.g., AndroidTTS)
             return
         }
@@
         // fallback to local Android TTS manager as a last resort
-        ttsManager.speak(text)
+        // When Android TTS speaks, simulate speaking avatar state
+        _state.value = _state.value.copy(avatarState = AvatarState.Speaking)
+        ttsManager.speak(text)
+        // Note: Android TTS does not provide a completion callback here, rely on TtsManager internals or reset state after a delay in future.
     }
*** End Patch
