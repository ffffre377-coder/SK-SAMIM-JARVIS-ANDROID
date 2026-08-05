package com.samim.jarvis.automation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AutomationManager: top-level interface for reminders, scheduled tasks and quick actions.
 */
interface AutomationManager {
    suspend fun scheduleReminder(reminder: Reminder): Boolean
    suspend fun cancelReminder(id: String): Boolean
}
