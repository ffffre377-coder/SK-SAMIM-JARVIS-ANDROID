package com.samim.jarvis.memory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY lastUpdated DESC")
    suspend fun list(): List<Conversation>

    @Insert
    suspend fun insert(conversation: Conversation): Long
}
