package com.samim.jarvis.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiManager @Inject constructor() {
    private fun baseClient(apiKey: String? = null): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (!apiKey.isNullOrEmpty()) {
            val interceptor = Interceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder = original.newBuilder()
                requestBuilder.header("Authorization", "Bearer $apiKey")
                requestBuilder.header("Content-Type", "application/json")
                chain.proceed(requestBuilder.build())
            }
            builder.addInterceptor(interceptor)
        }
        return builder.build()
    }

    fun makeOpenAiService(baseUrl: String, apiKey: String? = null): OpenAIService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(baseClient(apiKey))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(OpenAIService::class.java)
    }

    fun makeGeminiService(baseUrl: String, apiKey: String? = null): GeminiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(baseClient(apiKey))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(GeminiService::class.java)
    }
}
