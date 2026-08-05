package com.samim.jarvis.voice.assistants

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.samim.jarvis.security.SecureStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * CommandRouter: lightweight router that maps free-form text to assistant actions (file, media, coding, search)
 * Uses simple patterns and delegates to assistant implementations. All actions that are sensitive require confirmation.
 */
class CommandRouter(private val context: Context, private val secureStorage: SecureStorage) {

    private val fileAssistant: FileAssistant by lazy { FileAssistantImpl(context) }
    private val mediaAssistant: MediaAssistant by lazy { MediaAssistantImpl(context) }
    private val codingAssistant: CodingAssistant by lazy { CodingAssistantImpl(context, secureStorage) }

    /**
     * Process a user utterance. If it matches a local assistant action, requestConfirmation will be invoked.
     * requestConfirmation(title, message, onConfirm)
     * onResult(success, message)
     */
    fun process(
        text: String,
        requestConfirmation: (String, String, () -> Unit) -> Unit,
        onResult: (Boolean, String) -> Unit
    ): Boolean {
        val lowered = text.lowercase()
        // File commands
        if (lowered.contains("find my file") || lowered.contains("find my pdf") || lowered.contains("open my documents")) {
            val q = extractQuery(lowered, listOf("find my file", "find my pdf", "open my documents"))
            requestConfirmation("Find files", "Search for '$q' on your device?", {
                CoroutineScope(Dispatchers.Main).launch {
                    val results = fileAssistant.searchFiles(q ?: "")
                    if (results.isNotEmpty()) {
                        // open first result
                        fileAssistant.openFile(results.first())
                        onResult(true, "Opened ${results.first()}")
                    } else {
                        onResult(false, "No files found for $q")
                    }
                }
            })
            return true
        }

        if (lowered.contains("send this file") || lowered.contains("send this photo") || lowered.contains("send this")) {
            // In real flow, we need context of the current file; here we prompt user to pick
            requestConfirmation("Share file", "Do you want to share the last captured photo or selected file?", {
                CoroutineScope(Dispatchers.Main).launch {
                    // Attempt to find recent image
                    val recent = mediaAssistant.searchMedia("").firstOrNull()
                    if (recent != null) {
                        mediaAssistant.shareMedia(recent)
                        onResult(true, "Sharing started")
                    } else {
                        onResult(false, "No recent media found to share")
                    }
                }
            })
            return true
        }

        // Media commands
        if (lowered.contains("play music") || lowered.contains("play song") || lowered.contains("play music")) {
            val q = extractQuery(lowered, listOf("play music", "play song", "play"))
            requestConfirmation("Play media", "Search and play '$q'?", {
                CoroutineScope(Dispatchers.Main).launch {
                    val list = mediaAssistant.searchMedia(q ?: "")
                    if (list.isNotEmpty()) {
                        mediaAssistant.playMedia(list.first())
                        onResult(true, "Playing ${list.first()}")
                    } else {
                        onResult(false, "No media found for $q")
                    }
                }
            })
            return true
        }

        // Coding commands
        if (lowered.contains("create python code") || lowered.contains("create python") || lowered.matches(Regex("create .* code"))) {
            // extract language
            val lang = when {
                lowered.contains("python") -> "python"
                lowered.contains("java") -> "java"
                lowered.contains("kotlin") -> "kotlin"
                lowered.contains("javascript") -> "javascript"
                lowered.contains("html") -> "html"
                else -> "python"
            }
            requestConfirmation("Generate Code", "Generate a $lang template?", {
                CoroutineScope(Dispatchers.Main).launch {
                    val template = codingAssistant.generateTemplate(lang, "NewProject")
                    val id = codingAssistant.saveSnippet("$lang template", template)
                    onResult(true, "Generated $lang template and saved as snippet $id")
                }
            })
            return true
        }

        // Smart search: apps
        if (lowered.contains("open settings") || lowered.contains("find app") || lowered.contains("open")) {
            val appQuery = extractQuery(lowered, listOf("open", "find app", "open settings"))
            if (!appQuery.isNullOrBlank()) {
                requestConfirmation("Open app", "Open app matching '$appQuery'?", {
                    CoroutineScope(Dispatchers.Main).launch {
                        val pm = context.packageManager
                        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                        val match = apps.firstOrNull { it.loadLabel(pm).toString().lowercase().contains(appQuery) }
                        if (match != null) {
                            val launch = pm.getLaunchIntentForPackage(match.packageName)
                            if (launch != null) {
                                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(launch)
                                onResult(true, "Opened ${match.loadLabel(pm)}")
                            } else {
                                onResult(false, "Cannot launch ${match.loadLabel(pm)}")
                            }
                        } else {
                            onResult(false, "No app found matching $appQuery")
                        }
                    }
                })
                return true
            }
        }

        return false
    }

    private fun extractQuery(text: String, patterns: List<String>): String? {
        for (p in patterns) {
            if (text.contains(p)) {
                val idx = text.indexOf(p) + p.length
                val rest = text.substring(idx).trim()
                return if (rest.isBlank()) null else rest
            }
        }
        return null
    }
}
