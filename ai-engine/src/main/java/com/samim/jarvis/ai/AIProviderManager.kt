package com.samim.jarvis.ai

class AllProvidersFailedException(val errors: List<Pair<String, Throwable>>) : Exception("All providers failed: ${errors.map { it.first }}")

class AIProviderManager(private val providers: List<AIProvider>) {

    // Start with all providers enabled by default
    private val enabled = providers.map { it.name }.toMutableSet()

    suspend fun sendWithFallback(payload: Map<String, Any>): Any {
        val errors = mutableListOf<Pair<String, Throwable>>()
        for (provider in providers) {
            if (!enabled.contains(provider.name)) continue
            try {
                return provider.sendMessage(payload)
            } catch (t: Throwable) {
                errors.add(provider.name to t)
            }
        }
        throw AllProvidersFailedException(errors)
    }

    fun enableProvider(name: String, isEnabled: Boolean) {
        if (isEnabled) enabled.add(name) else enabled.remove(name)
    }

    fun setEnabledProviders(names: Set<String>) {
        enabled.clear()
        enabled.addAll(names)
    }

    fun getEnabledProviders(): Set<String> = enabled.toSet()

    fun listProviders(): List<String> = providers.map { it.name }
}
