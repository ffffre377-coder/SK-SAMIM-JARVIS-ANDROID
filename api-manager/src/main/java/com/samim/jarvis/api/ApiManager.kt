package com.samim.jarvis.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiManager @Inject constructor() {
    private val client = OkHttpClient.Builder().build()

    fun makeOpenAiService(baseUrl: String): OpenAIService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(OpenAIService::class.java)
    }

    fun makeGeminiService(baseUrl: String): GeminiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(GeminiService::class.java)
    }
}
