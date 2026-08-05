package com.samim.jarvis.voice.command

import java.util.Locale

/**
 * CommandParser: lightweight rule-based parser for voice commands.
 * Supports English and basic Hindi phrases. Returns a Command or Command.Unknown.
 */
object CommandParser {

    fun parse(utteranceRaw: String, locale: Locale = Locale.ENGLISH): Command {
        if (utteranceRaw.isBlank()) return Command.Unknown
        val utterance = utteranceRaw.trim().lowercase(locale)

        // 1) Open YouTube
        if (utterance.contains("youtube") || utterance.contains("you tube")) {
            // try to extract query like "play cats on youtube" or "search youtube for cats"
            val se = Regex("(?:search (youtube )?for|play|play on youtube|on youtube|youtube for) (.+)")
            val m = se.find(utterance)
            val query = m?.groups?.get(2)?.value ?: run {
                // fallback: remove 'open'/'launch' words
                utterance.replace("open youtube", "").replace("launch youtube", "").trim().ifEmpty { null }
            }
            return Command.OpenYouTube(query)
        }

        // 2) Open camera
        if (utterance.contains("open camera") || utterance.contains("camera") || utterance.contains("take a photo") || utterance.contains("photo")) {
            return Command.OpenCamera
        }

        // 3) Share file
        if (utterance.contains("share") && (utterance.contains("file") || utterance.contains("photo") || utterance.contains("image") || utterance.contains("document"))) {
            return Command.ShareFile
        }

        // 4) Toggle flashlight
        val flashOn = Regex("(turn|switch) (on|off) (the )?(flash|flashlight|torch)")
        val flashToggle = Regex("(flash|flashlight|torch) (on|off|toggle)")
        val mFlash = flashOn.find(utterance) ?: flashToggle.find(utterance)
        if (mFlash != null) {
            val g = mFlash.groups
            val valText = g[2]?.value ?: g[1]?.value
            val turnOn = when (valText) {
                "on" -> true
                "off" -> false
                "toggle" -> null
                else -> null
            }
            return Command.ToggleFlashlight(turnOn)
        }

        // 5) Volume control
        val setVol = Regex("set volume to (\\d+)")
        val mVol = setVol.find(utterance)
        if (mVol != null) {
            val level = mVol.groups[1]?.value?.toIntOrNull()
            return Command.SetVolume(level, null)
        }
        if (utterance.contains("volume up") || utterance.contains("increase volume") || utterance.contains("turn up the volume") || utterance.contains("volume increase")) {
            return Command.SetVolume(null, "up")
        }
        if (utterance.contains("volume down") || utterance.contains("decrease volume") || utterance.contains("turn down the volume") || utterance.contains("volume decrease")) {
            return Command.SetVolume(null, "down")
        }

        // 6) Call contact (English + simple Hindi)
        // Patterns: "call alice", "call to alice", "please call john", Hindi: "call karo alice", "alice ko call karo"
        val callRegex = Regex("(?:call|call to|please call|make a call to|make call to|call karo|ko call karo)\\s+([\\w \\\u0900-\\u097F\\-_.]+)")
        val mCall = callRegex.find(utterance)
        if (mCall != null) {
            val name = mCall.groups[1]?.value?.trim()
            return Command.CallContact(name)
        }

        // 7) Send message / text
        // English: "send a message to alice saying hello" or "text alice hello"
        val sendMsgRegex = Regex("(?:send( a)? message to|text|message)\\s+([\\w \\\u0900-\\u097F\\-_.]+)(?: (?:saying|that says|saying:|says) )?(.+)?")
        val mMsg = sendMsgRegex.find(utterance)
        if (mMsg != null) {
            val name = mMsg.groups[2]?.value?.trim()
            val msg = mMsg.groups[3]?.value?.trim()?.ifEmpty { null }
            return Command.SendMessage(name, msg)
        }

        // 8) Open app (try launch/ open)
        // Avoid matching generic phrases already caught above
        val openAppRegex = Regex("(?:open|launch|start|khol|kholna)\\s+([\\w \\\u0900-\\u097F\\-_.]+)")
        val mLaunch = openAppRegex.find(utterance)
        if (mLaunch != null) {
            val appName = mLaunch.groups[1]?.value?.trim()
            // ignore vague 'open camera' which we handled earlier
            if (!appName.isNullOrBlank() && !appName.contains("camera") && !appName.contains("youtube") && !appName.contains("flash") && !appName.contains("volume")) {
                return Command.LaunchApp(appName)
            }
        }

        // Additional Hindi patterns (simple)
        // "youtube kholo" -> handled by youtube branch because contains "youtube"
        if (utterance.contains("khol") || utterance.contains("kholen") || utterance.contains("karo")) {
            // fallback: try to extract app name after these words
            val hindiOpen = Regex("(?:khol|kholen|karo)\\s+([\\w \\\u0900-\\u097F\\-_.]+)")
            val m = hindiOpen.find(utterance)
            if (m != null) {
                val appName = m.groups[1]?.value?.trim()
                if (!appName.isNullOrBlank()) return Command.LaunchApp(appName)
            }
        }

        // If nothing matched, Unknown
        return Command.Unknown
    }
}
