*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/VoiceAssistantScreen.kt
@@
-        if (state.listening) WaveformBar(listening = true)
+        if (state.listening) WaveformBar(listening = true)
+        // Observe pending selection UI
+        val pending by vm.pendingSelection.collectAsState()
+        pending?.let { p ->
+            when (p) {
+                is com.samim.jarvis.voice.PendingSelection.Contact -> {
+                    ContactSelectionScreen(results = p.matches.map { it.first to it.second }, onSelect = { phone -> vm.acceptSelectionContact(phone) }, onCancel = { vm.clearPendingSelection() })
+                }
+                is com.samim.jarvis.voice.PendingSelection.File -> {
+                    FileSelectionScreen(results = p.matches.map { it.first to it.second }, onSelect = { uri -> vm.acceptSelectionFile(uri) }, onCancel = { vm.clearPendingSelection() })
+                }
+                is com.samim.jarvis.voice.PendingSelection.App -> {
+                    AppSelectionScreen(results = p.matches, onSelect = { pkg -> vm.acceptSelectionApp(pkg) }, onCancel = { vm.clearPendingSelection() })
+                }
+            }
+        }
*** End Patch
