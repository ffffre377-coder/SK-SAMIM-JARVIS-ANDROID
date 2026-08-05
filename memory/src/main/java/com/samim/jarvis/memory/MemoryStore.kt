package com.samim.jarvis.memory

/**
 * MemoryStore: interface for storing and retrieving long-term memory points.
 * Implementations should ensure data is persisted securely when needed.
 */
interface MemoryStore {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun remove(key: String)
    suspend fun listKeys(prefix: String? = null): List<String>
}
