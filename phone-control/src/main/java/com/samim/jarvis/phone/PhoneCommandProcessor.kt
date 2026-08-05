package com.samim.jarvis.phone

import android.content.Context
import android.text.TextUtils
import java.util.Locale

/**
 * Minimal natural-language command parser for phone control.
 * Supports "open <app>" and "set volume to <n>" patterns.
 */
class PhoneCommandProcessor(private val context: Context) {

    private val phoneControl = PhoneControlManager(context)

    fun process(
        command: String,
        requestConfirmation: (title: String, message: String, onConfirm: () -> Unit) -> Unit,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        if (TextUtils.isEmpty(command)) {
            onResult(false, "Empty command")
            return
        }

        val text = command.trim().lowercase(Locale.getDefault())

        when {
            text.startsWith("open ") -> {
                val target = command.substringAfter("open").trim()
                if (target.isEmpty()) {
                    onResult(false, "Specify app to open")
                    return
                }

                val title = "Open app?"
                val message = "Do you want to open \"$target\"?"
                requestConfirmation(title, message) {
                    val ok = phoneControl.openApp(target)
                    if (ok) onResult(true, "Opening $target")
                    else onResult(false, "Could not open $target (app not found)")
                }
            }

            text.contains("set volume to") -> {
                val numText = text.substringAfter("set volume to").trim().split(" ")[0]
                val maybeNum = numText.toIntOrNull()
                if (maybeNum == null) {
                    onResult(false, "Couldn't parse volume value")
                    return
                }

                val title = "Set volume?"
                val message = "Do you want to set volume to $maybeNum?"
                requestConfirmation(title, message) {
                    phoneControl.setVolume(maybeNum)
                    onResult(true, "Volume set to $maybeNum")
                }
            }

            else -> {
                onResult(false, "Command not recognized")
            }
        }
    }
}
