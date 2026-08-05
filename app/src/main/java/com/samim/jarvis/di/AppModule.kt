package com.samim.jarvis.di

import android.content.Context
import androidx.room.Room
import com.samim.jarvis.ai.AIProviderManager
import com.samim.jarvis.ai.GeminiAdapter
import com.samim.jarvis.ai.OpenAIAdapter
import com.samim.jarvis.api.ApiManager
import com.samim.jarvis.memory.AppDatabase
import com.samim.jarvis.memory.ConversationDao
import com.samim.jarvis.memory.MessageDao
import com.samim.jarvis.security.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "jarvis.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()

    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()

    @Provides
    @Singleton
    fun provideSecureStorage(@ApplicationContext context: Context): SecureStorage = SecureStorage(context)

    @Provides
    @Singleton
    fun provideAIProviderManager(apiManager: ApiManager, secureStorage: SecureStorage): AIProviderManager {
        val providers = listOf(
            OpenAIAdapter(apiManager, secureStorage),
            GeminiAdapter(apiManager, secureStorage)
        )
        return AIProviderManager(providers)
    }
}
