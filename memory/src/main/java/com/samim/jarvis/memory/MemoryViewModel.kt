package com.samim.jarvis.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MemoryUiState(
    val entries: List<MemoryEntry> = emptyList(),
    val statusMessage: String = ""
)

data class MemoryEntry(val key: String, val value: String)

class MemoryViewModel(private val secureStorage: SecureStorage) : ViewModel() {
    private val repo = EncryptedMemoryRepository(secureStorage)

    private val _state = MutableStateFlow(MemoryUiState())
    val state: StateFlow<MemoryUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val keys = repo.listKeys()
            val entries = keys.mapNotNull { k ->
                val v = repo.getString(k)
                if (v.isNullOrBlank()) null else MemoryEntry(k, v)
            }
            _state.value = _state.value.copy(entries = entries)
        }
    }

    suspend fun clearKey(key: String) {
        repo.remove(key)
        refresh()
    }

    suspend fun clearAll() {
        val keys = repo.listKeys()
        for (k in keys) repo.remove(k)
        refresh()
    }

    suspend fun putNickName(nick: String) {
        repo.putString("nickname", nick)
        refresh()
    }

    suspend fun getNickName(): String? = repo.getString("nickname")
}
