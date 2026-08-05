package com.samim.jarvis.memory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun listForConversation(conversationId: Long): List<Message>

    @Insert
    suspend fun insert(message: Message): Long
}
