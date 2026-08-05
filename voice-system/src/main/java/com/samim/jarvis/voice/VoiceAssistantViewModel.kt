*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/VoiceAssistantViewModel.kt
@@
 package com.samim.jarvis.voice
 
 import androidx.compose.runtime.mutableStateOf
 import androidx.lifecycle.ViewModel
+import androidx.lifecycle.viewModelScope
 import kotlinx.coroutines.Dispatchers
 import kotlinx.coroutines.flow.MutableStateFlow
 import kotlinx.coroutines.flow.StateFlow
+import kotlinx.coroutines.flow.collect
+import kotlinx.coroutines.flow.collectLatest
+import kotlinx.coroutines.launch
+import kotlinx.coroutines.delay
@@
 class VoiceAssistantViewModel(private val secureStorage: com.samim.jarvis.security.SecureStorage) : ViewModel() {
@@
-    private val _state = MutableStateFlow(VoiceUiState())
-    val state: StateFlow<VoiceUiState> = _state
+    private val _state = MutableStateFlow(VoiceUiState())
+    val state: StateFlow<VoiceUiState> = _state
+
+    // Pending selection flows (for contact/file/app disambiguation)
+    sealed class PendingSelection {
+        data class Contact(val matches: List<Pair<String, String>>, val onChosen: (String) -> Unit) : PendingSelection()
+        data class File(val matches: List<Pair<String, android.net.Uri>>, val onChosen: (android.net.Uri) -> Unit) : PendingSelection()
+        data class App(val matches: List<Pair<String, String>>, val onChosen: (String) -> Unit) : PendingSelection()
+    }
+
+    private val _pendingSelection = MutableStateFlow<PendingSelection?>(null)
+    val pendingSelection: StateFlow<PendingSelection?> = _pendingSelection
+
+    private var pendingSelectionJob: kotlinx.coroutines.Job? = null
+    private val selectionTimeoutMs = 2 * 60 * 1000L // 2 minutes
@@
     init {
@@
         sttManager.setListener(object : SpeechToTextManager.Listener {
@@
         })
+
+        // Register to SelectionBroadcaster to receive selection requests from CommandRouter
+        SelectionBroadcaster.registerContactListener { matches, continuation ->
+            showContactSelection(matches, continuation)
+        }
+        SelectionBroadcaster.registerFileListener { matches, continuation ->
+            showFileSelection(matches, continuation)
+        }
+        SelectionBroadcaster.registerAppListener { matches, continuation ->
+            showAppSelection(matches, continuation)
+        }
     }
@@
     fun stopListening() {
         sttManager.stopListening()
         _state.value = _state.value.copy(listening = false, avatarState = AvatarState.Idle)
     }
+
+    fun showContactSelection(matches: List<Pair<String, String>>, onChosen: (String)->Unit) {
+        pendingSelectionJob?.cancel()
+        _pendingSelection.value = PendingSelection.Contact(matches, onChosen)
+        pendingSelectionJob = viewModelScope.launch {
+            delay(selectionTimeoutMs)
+            clearPendingSelection()
+        }
+    }
+
+    fun showFileSelection(matches: List<Pair<String, android.net.Uri>>, onChosen: (android.net.Uri)->Unit) {
+        pendingSelectionJob?.cancel()
+        _pendingSelection.value = PendingSelection.File(matches, onChosen)
+        pendingSelectionJob = viewModelScope.launch {
+            delay(selectionTimeoutMs)
+            clearPendingSelection()
+        }
+    }
+
+    fun showAppSelection(matches: List<Pair<String, String>>, onChosen: (String)->Unit) {
+        pendingSelectionJob?.cancel()
+        _pendingSelection.value = PendingSelection.App(matches, onChosen)
+        pendingSelectionJob = viewModelScope.launch {
+            delay(selectionTimeoutMs)
+            clearPendingSelection()
+        }
+    }
+
+    fun acceptSelectionContact(phone: String) {
+        val pending = _pendingSelection.value
+        if (pending is PendingSelection.Contact) {
+            pending.onChosen(phone)
+        }
+        clearPendingSelection()
+    }
+
+    fun acceptSelectionFile(uri: android.net.Uri) {
+        val pending = _pendingSelection.value
+        if (pending is PendingSelection.File) {
+            pending.onChosen(uri)
+        }
+        clearPendingSelection()
+    }
+
+    fun acceptSelectionApp(pkg: String) {
+        val pending = _pendingSelection.value
+        if (pending is PendingSelection.App) {
+            pending.onChosen(pkg)
+        }
+        clearPendingSelection()
+    }
+
+    fun clearPendingSelection() {
+        pendingSelectionJob?.cancel()
+        pendingSelectionJob = null
+        _pendingSelection.value = null
+    }
*** End Patch
