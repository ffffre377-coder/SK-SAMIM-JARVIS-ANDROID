package com.samim.jarvis.voice

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samim.jarvis.security.SecureStorage
import com.samim.jarvis.voice.assistants.CommandRouter
import com.samim.jarvis.voice.personality.DefaultPersonalityEngine
import com.samim.jarvis.voice.personality.PersonalityEngine
import com.samim.jarvis.voice.personality.PersonalityMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val PENDING_SELECTION_TIMEOUT_MS = 30_000L

/**
 * A selection request awaiting user input in the UI (multiple contacts/files/apps matched a command).
 */
sealed class PendingSelection {
    data class Contact(val matches: List<Pair<String, String>>, val onChosen: (String) -> Unit) : PendingSelection()
    data class File(val matches: List<Pair<String, Uri>>, val onChosen: (Uri) -> Unit) : PendingSelection()
    data class App(val matches: List<Pair<String, String>>, val onChosen: (String) -> Unit) : PendingSelection()
}

/**
 * Central ViewModel for the voice assistant: owns wake-word/STT/TTS lifecycle, routes recognized
 * speech through [CommandRouter], and surfaces UI state + pending selection prompts.
 */
class VoiceAssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val secureStorage = SecureStorage(application)
    private val ttsManager = TextToSpeechManager(application)
    private val sttManager = SpeechToTextManager(application)
    private val wakeWordManager = WakeWordManager(application)
    private val personalityEngine: PersonalityEngine = DefaultPersonalityEngine()
    private val commandRouter = CommandRouter(application, secureStorage)

    private val _state = MutableStateFlow(
        VoiceUiState(
            wakeWordEnabled = secureStorage.getString("wake_word_enabled") == "true",
            personalityMode = personalityEngine.getMode().name
        )
    )
    val state: StateFlow<VoiceUiState> = _state

    private val _events = MutableSharedFlow<VoiceEvent>()
    val events: SharedFlow<VoiceEvent> = _events

    private val _pendingSelection = MutableStateFlow<PendingSelection?>(null)
    val pendingSelection: StateFlow<PendingSelection?> = _pendingSelection

    private var pendingSelectionJob: Job? = null

    init {
        wakeWordManager.enable(_state.value.wakeWordEnabled)

        sttManager.setListener(object : SpeechToTextManager.Listener {
            override fun onPartialResult(text: String) {
                _state.value = _state.value.copy(lastTranscript = text)
            }

            override fun onResult(text: String) {
                _state.value = _state.value.copy(
                    listening = false,
                    avatarState = AvatarState.Thinking,
                    lastTranscript = text
                )
                handleUserUtterance(text)
            }

            override fun onError(error: String) {
                _state.value = _state.value.copy(
                    listening = false,
                    avatarState = AvatarState.Idle,
                    statusMessage = error
                )
            }
        })

        SelectionBroadcaster.registerContactListener { matches, onChosen ->
            showPendingSelection(PendingSelection.Contact(matches, onChosen))
        }
        SelectionBroadcaster.registerFileListener { matches, onChosen ->
            showPendingSelection(PendingSelection.File(matches, onChosen))
        }
        SelectionBroadcaster.registerAppListener { matches, onChosen ->
            showPendingSelection(PendingSelection.App(matches, onChosen))
        }
    }

    private fun showPendingSelection(selection: PendingSelection) {
        pendingSelectionJob?.cancel()
        _pendingSelection.value = selection
        pendingSelectionJob = viewModelScope.launch {
            delay(PENDING_SELECTION_TIMEOUT_MS)
            _pendingSelection.value = null
        }
    }

    fun startListening() {
        _state.value = _state.value.copy(listening = true, avatarState = AvatarState.Listening)
        sttManager.startListening()
    }

    fun stopListening() {
        sttManager.stopListening()
        _state.value = _state.value.copy(listening = false, avatarState = AvatarState.Idle)
    }

    private fun handleUserUtterance(text: String) {
        val handled = commandRouter.process(
            text = text,
            requestConfirmation = { title, message, onConfirm ->
                viewModelScope.launch { _events.emit(VoiceEvent.ConfirmAction(title, message)) }
                // TODO: wire this up to a real confirmation dialog; auto-confirming for now
                // keeps voice commands usable hands-free.
                onConfirm()
            },
            onResult = { _, message ->
                _state.value = _state.value.copy(
                    avatarState = AvatarState.Speaking,
                    lastResponse = message,
                    statusMessage = message
                )
                speak(message)
            }
        )
        if (!handled) {
            _state.value = _state.value.copy(
                avatarState = AvatarState.Idle,
                lastResponse = "Sorry, I didn't understand that."
            )
        }
    }

    fun speak(text: String) {
        viewModelScope.launch {
            ttsManager.speak(text)
            _state.value = _state.value.copy(avatarState = AvatarState.Idle)
        }
    }

    fun setPersonality(mode: PersonalityMode) {
        personalityEngine.setMode(mode)
        _state.value = _state.value.copy(personalityMode = mode.name)
    }

    fun setWakeWordEnabled(enabled: Boolean) {
        secureStorage.putString("wake_word_enabled", enabled.toString())
        wakeWordManager.enable(enabled)
        _state.value = _state.value.copy(wakeWordEnabled = enabled)
    }

    fun acceptSelectionContact(phone: String) {
        val current = _pendingSelection.value as? PendingSelection.Contact ?: return
        clearPendingSelection()
        current.onChosen(phone)
    }

    fun acceptSelectionFile(uri: Uri) {
        val current = _pendingSelection.value as? PendingSelection.File ?: return
        clearPendingSelection()
        current.onChosen(uri)
    }

    fun acceptSelectionApp(pkg: String) {
        val current = _pendingSelection.value as? PendingSelection.App ?: return
        clearPendingSelection()
        current.onChosen(pkg)
    }

    fun clearPendingSelection() {
        pendingSelectionJob?.cancel()
        pendingSelectionJob = null
        _pendingSelection.value = null
    }

    // Permission toggles persisted in secure storage
    fun setPermissionToggle(key: String, enabled: Boolean) {
        secureStorage.putString(key, enabled.toString())
    }

    fun isPermissionEnabled(key: String): Boolean {
        return secureStorage.getString(key) == "true"
    }

    override fun onCleared() {
        super.onCleared()
        SelectionBroadcaster.unregisterContactListener()
        SelectionBroadcaster.unregisterFileListener()
        SelectionBroadcaster.unregisterAppListener()
        sttManager.release()
        ttsManager.shutdown()
    }
}
