package com.samim.jarvis.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun testConnection(baseUrl: String, apiKey: String?, testPath: String = "v1/models"): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val client = baseClient(apiKey)
                val url = if (baseUrl.endsWith("/")) baseUrl + testPath else "$baseUrl/$testPath"
                val request = Request.Builder().url(url).get().build()
                client.newCall(request).execute().use { resp ->
                    if (resp.isSuccessful) {
                        Result.success("Success: ${resp.code}")
                    } else {
                        Result.failure(Exception("HTTP ${resp.code}: ${resp.message}"))
                    }
                }
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
    }
}
