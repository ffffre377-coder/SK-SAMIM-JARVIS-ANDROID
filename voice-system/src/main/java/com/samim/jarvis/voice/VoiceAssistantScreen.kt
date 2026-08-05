*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/VoiceAssistantScreen.kt
@@
     if (showConfirm) {
@@
     }
 
     // Existing UI placeholders (keep minimal to avoid breaking)
     Column {
-        Text("Voice Assistant")
+        // Premium HUD
+        Row(modifier = androidx.compose.ui.Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
+            JarvisAvatar(state = if (state.listening) AvatarState.Listening else AvatarState.Idle, modifier = Modifier.padding(8.dp))
+            Column {
+                Text("Voice Assistant")
+                Text("Mode: ${state.personalityMode ?: "Friendly"}")
+            }
+        }
+        if (state.listening) WaveformBar(listening = true)
         Row(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
             Button(onClick = { vm.startListening() }) { Text("Start Listening") }
             Button(onClick = { vm.stopListening() }) { Text("Stop") }
         }
@@
     }
 }
*** End Patch
