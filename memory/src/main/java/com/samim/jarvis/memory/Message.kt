package com.samim.jarvis.memory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val sender: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
