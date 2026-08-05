package com.samim.jarvis.ai

class AllProvidersFailedException(val errors: List<Pair<String, Throwable>>) : Exception("All providers failed: ${errors.map { it.first }}")

class AIProviderManager(private val providers: List<AIProvider>) {

    suspend fun sendWithFallback(payload: Map<String, Any>): Any {
        val errors = mutableListOf<Pair<String, Throwable>>()
        for (provider in providers) {
            try {
                return provider.sendMessage(payload)
            } catch (t: Throwable) {
                errors.add(provider.name to t)
            }
        }
        throw AllProvidersFailedException(errors)
    }
}
