*** Begin Patch
*** Update File: ai-engine/src/main/java/com/samim/jarvis/ai/ChatRepository.kt
@@
 @Singleton
 class ChatRepository @Inject constructor(
     private val providerManager: AIProviderManager,
     private val conversationDao: ConversationDao,
     private val messageDao: MessageDao
 ) {
+
+    init {
+        // register globally so non-DI components can access AI (pragmatic bridge)
+        try {
+            com.samim.jarvis.ai.ChatRepositoryHolder.register(this)
+        } catch (e: Exception) {
+            // ignore registration failures
+        }
+    }
*** End Patch
