package com.samim.jarvis.automation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AutomationUiState(
    val reminders: List<Reminder> = emptyList(),
    val statusMessage: String = ""
)

class AutomationViewModel(private val secureStorage: SecureStorage) : ViewModel() {
    private val repo = AutomationRepository(secureStorage)
    private val _state = MutableStateFlow(AutomationUiState())
    val state: StateFlow<AutomationUiState> = _state

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val list = repo.loadReminders()
            _state.value = _state.value.copy(reminders = list)
        }
    }

    suspend fun createSampleReminder() {
        val id = UUID.randomUUID().toString()
        val r = Reminder(id = id, title = "Sample", body = "This is a sample reminder", timeEpochMs = System.currentTimeMillis() + 60000)
        val list = repo.loadReminders().toMutableList()
        list.add(r)
        repo.saveReminders(list)
        refresh()
    }

    suspend fun cancelReminder(id: String) {
        val list = repo.loadReminders().filterNot { it.id == id }
        repo.saveReminders(list)
        refresh()
    }
}
