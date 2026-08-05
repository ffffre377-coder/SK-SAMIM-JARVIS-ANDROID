package com.samim.jarvis.automation

import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Simple AutomationRepository using SecureStorage to persist reminders as JSON.
 * For production, replace with Room + WorkManager scheduling.
 */
class AutomationRepository(private val secureStorage: SecureStorage) {

    private fun key() = "automation.reminders"

    suspend fun saveReminders(reminders: List<Reminder>) {
        withContext(Dispatchers.IO) {
            val raw = Json.encodeToString(reminders)
            secureStorage.putString(key(), raw)
        }
    }

    suspend fun loadReminders(): List<Reminder> = withContext(Dispatchers.IO) {
        val raw = secureStorage.getString(key()) ?: return@withContext emptyList()
        return@withContext try { Json.decodeFromString(raw) } catch (e: Exception) { emptyList() }
    }
}
