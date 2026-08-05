*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/assistants/CommandRouter.kt
@@
-    private val fileAssistant: FileAssistant by lazy { FileAssistantImpl(context) }
+    private val fileAssistant: FileAssistant by lazy { FileAssistantWithDocuments(context) }
@@
-    private val codingAssistant: CodingAssistant by lazy { CodingAssistantImpl(context, secureStorage) }
+    private val codingAssistant: CodingAssistant by lazy { CodingAssistantImpl(context, secureStorage, null) }
*** End Patch
