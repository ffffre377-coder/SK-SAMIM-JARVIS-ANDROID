*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/assistants/CommandRouter.kt
@@
-class CommandRouter(private val context: Context, private val secureStorage: SecureStorage) {
+class CommandRouter(private val context: Context, private val secureStorage: SecureStorage) {
@@
-    /**
-     * Process a user utterance. If it matches a local assistant action, requestConfirmation will be invoked.
-     * requestConfirmation(title, message, onConfirm)
-     * onResult(success, message)
-     */
+    interface SelectionListener {
+        fun onContactSelection(matches: List<Pair<String, String>>)
+        fun onAppSelection(matches: List<Pair<String, String>>)
+        fun onFileSelection(matches: List<Pair<String, String>>)
+    }
+
+    private var selectionListener: SelectionListener? = null
+
+    fun setSelectionListener(l: SelectionListener?) {
+        selectionListener = l
+    }
+
+    /**
+     * Process a user utterance. If it matches a local assistant action, requestConfirmation will be invoked.
+     * requestConfirmation(title, message, onConfirm)
+     * onResult(success, message)
+     */
     fun process(
         text: String,
         requestConfirmation: (String, String, () -> Unit) -> Unit,
         onResult: (Boolean, String) -> Unit
     ): Boolean {
@@
-                    val matches = ContactSearch.findContactsByName(context, contact)
-                    if (matches.isEmpty()) {
-                        onResult(false, "Could not find contact $contact")
-                        return@launch
-                    }
-                    if (matches.size == 1) {
-                        val phone = matches.first().second
-                        WhatsAppHelper.sendTextToContactViaApi(context, phone, "")
-                        onResult(true, "Opened WhatsApp chat for $contact")
-                        return@launch
-                    }
-                    // Multiple matches — request UI selection via stored callback in SecureStorage (the ViewModel/UI will read it)
-                    // For simplicity, save matches temporarily and signal UI to open selection (pragmatic approach)
-                    val tempKey = "contact_selection_temp"
-                    val payload = matches.joinToString(";;") { "${it.first}:::${it.second}" }
-                    com.samim.jarvis.security.SecureStorageProvider.get(context).putString(tempKey, payload)
-                    // Signal that UI should open selection - we rely on the hosting UI to poll or check this key
-                    onResult(true, "Multiple contacts found for $contact — please select in UI")
+                    val matches = ContactSearch.findContactsByName(context, contact)
+                    if (matches.isEmpty()) {
+                        onResult(false, "Could not find contact $contact")
+                        return@launch
+                    }
+                    if (matches.size == 1) {
+                        val phone = matches.first().second
+                        WhatsAppHelper.sendTextToContactViaApi(context, phone, "")
+                        onResult(true, "Opened WhatsApp chat for $contact")
+                        return@launch
+                    }
+                    // Multiple matches — delegate selection to UI via SelectionListener
+                    selectionListener?.onContactSelection(matches)
+                    onResult(true, "Multiple contacts found for $contact — please select in UI")
                 }
             })
             return true
         }
*** End Patch
