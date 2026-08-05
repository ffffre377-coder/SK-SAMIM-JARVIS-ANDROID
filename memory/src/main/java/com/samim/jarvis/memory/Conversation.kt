package com.samim.jarvis.memory

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
