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
import com.samim.jarvis.voice.ElevenLabsTtsAdapter
import com.samim.jarvis.voice.GoogleTtsAdapter
import com.samim.jarvis.voice.PorcupineManager
import com.samim.jarvis.voice.SpeechToTextManager
import com.samim.jarvis.voice.TtsPlayback
import com.samim.jarvis.voice.TtsProvider
import com.samim.jarvis.voice.TtsProviderManager
import com.samim.jarvis.voice.TextToSpeechManager
import com.samim.jarvis.voice.providers.VoiceProviderRepository
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
    fun provideApiManager(): ApiManager = ApiManager()

    @Provides
    @Singleton
    fun provideTextToSpeechManager(@ApplicationContext context: Context): TextToSpeechManager = TextToSpeechManager(context)

    @Provides
    @Singleton
    fun provideSpeechToTextManager(@ApplicationContext context: Context): SpeechToTextManager = SpeechToTextManager(context)

    @Provides
    @Singleton
    fun providePorcupineManager(@ApplicationContext context: Context): PorcupineManager = PorcupineManager(context)

    @Provides
    @Singleton
    fun provideTtsPlayback(@ApplicationContext context: Context): TtsPlayback = TtsPlayback(context)

    @Provides
    @Singleton
    fun provideTtsProviders(secureStorage: SecureStorage): @JvmSuppressWildcards List<TtsProvider> {
        return listOf(
            ElevenLabsTtsAdapter(secureStorage),
            GoogleTtsAdapter(secureStorage)
        )
    }

    @Provides
    @Singleton
    fun provideTtsProviderManager(providers: @JvmSuppressWildcards List<TtsProvider>, secureStorage: SecureStorage): TtsProviderManager = TtsProviderManager(providers, secureStorage)

    @Provides
    @Singleton
    fun provideVoiceProviderRepository(secureStorage: SecureStorage): VoiceProviderRepository = VoiceProviderRepository(secureStorage)

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
