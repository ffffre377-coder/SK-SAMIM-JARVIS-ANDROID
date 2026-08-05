package com.samim.jarvis.memory

import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * EncryptedMemoryRepository: implements MemoryStore using SecureStorage (EncryptedSharedPreferences)
 * for storing small key/value facts like nickname and preferences.
 */
class EncryptedMemoryRepository(private val secureStorage: SecureStorage) : MemoryStore {

    private fun fullKey(key: String) = "memory.$key"

    override suspend fun putString(key: String, value: String) {
        withContext(Dispatchers.IO) {
            secureStorage.putString(fullKey(key), value)
        }
    }

    override suspend fun getString(key: String): String? = withContext(Dispatchers.IO) {
        secureStorage.getString(fullKey(key))
    }

    override suspend fun remove(key: String) {
        withContext(Dispatchers.IO) {
            secureStorage.putString(fullKey(key), "")
        }
    }

    override suspend fun listKeys(prefix: String?): List<String> = withContext(Dispatchers.IO) {
        // EncryptedSharedPreferences does not offer listing; so provide a small registry key to track stored keys.
        val registryKey = fullKey("_registry_keys")
        val raw = secureStorage.getString(registryKey)
        val keys = raw?.split(";")?.filter { it.isNotBlank() }?.toMutableList() ?: mutableListOf()
        if (prefix.isNullOrBlank()) return@withContext keys
        return@withContext keys.filter { it.startsWith(prefix) }
    }

    private suspend fun addToRegistry(key: String) {
        withContext(Dispatchers.IO) {
            val registryKey = fullKey("_registry_keys")
            val raw = secureStorage.getString(registryKey) ?: ""
            val keys = raw.split(";").filter { it.isNotBlank() }.toMutableList()
            if (!keys.contains(key)) {
                keys.add(key)
                secureStorage.putString(registryKey, keys.joinToString(";"))
            }
        }
    }

    override suspend fun putString(key: String, value: String) {
        withContext(Dispatchers.IO) {
            secureStorage.putString(fullKey(key), value)
            addToRegistry(key)
        }
    }
}
