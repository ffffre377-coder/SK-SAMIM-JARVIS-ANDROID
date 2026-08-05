package com.samim.jarvis.memory

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Conversation::class, Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
