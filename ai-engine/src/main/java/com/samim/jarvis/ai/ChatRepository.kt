package com.samim.jarvis.ai

import com.samim.jarvis.memory.ConversationDao
import com.samim.jarvis.memory.Message
import com.samim.jarvis.memory.MessageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val providerManager: AIProviderManager,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {

    suspend fun sendUserMessage(conversationId: Long, messageText: String): Result<Any> {
        return try {
            // persist user message
            val userMessage = Message(conversationId = conversationId, sender = "user", content = messageText)
            withContext(Dispatchers.IO) { messageDao.insert(userMessage) }

            // prepare payload (simple form)
            val payload = mapOf("messages" to listOf(mapOf("role" to "user", "content" to messageText)))

            val response = withContext(Dispatchers.IO) { providerManager.sendWithFallback(payload) }

            // persist assistant response as stringified
            val assistantText = response.toString()
            val assistantMessage = Message(conversationId = conversationId, sender = "assistant", content = assistantText)
            withContext(Dispatchers.IO) { messageDao.insert(assistantMessage) }

            Result.success(response)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
