*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/VoiceAssistantViewModel.kt
@@
     fun clearPendingSelection() {
         pendingSelectionJob?.cancel()
         pendingSelectionJob = null
         _pendingSelection.value = null
     }
+
+    // Permission toggles persisted in secure storage
+    fun setPermissionToggle(key: String, enabled: Boolean) {
+        secureStorage.putString(key, enabled.toString())
+    }
+
+    fun isPermissionEnabled(key: String): Boolean {
+        return secureStorage.getString(key) == "true"
+    }
*** End Patch
