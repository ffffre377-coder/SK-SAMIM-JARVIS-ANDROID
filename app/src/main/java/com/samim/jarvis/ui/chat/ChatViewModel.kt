package com.samim.jarvis.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samim.jarvis.ai.ChatRepository
import com.samim.jarvis.memory.Message
import com.samim.jarvis.memory.MessageDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(val messages: List<Message> = emptyList())

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val messageDao: MessageDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    private var conversationId: Long = 1 // default conversation; in future create/select

    init {
        viewModelScope.launch {
            val msgs = messageDao.listForConversation(conversationId)
            _uiState.value = ChatUiState(messages = msgs)
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + listOf(Message(conversationId = conversationId, sender = "user", content = text)))
            val result = chatRepository.sendUserMessage(conversationId, text)
            if (result.isSuccess) {
                val resp = result.getOrNull()
                val respText = resp?.toString() ?: ""
                // append assistant message locally
                _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + listOf(Message(conversationId = conversationId, sender = "assistant", content = respText)))
            } else {
                val err = result.exceptionOrNull()?.message ?: "Unknown error"
                _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + listOf(Message(conversationId = conversationId, sender = "system", content = "Error: $err")))
            }
        }
    }
}
