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

    private val fileAssistant: FileAssistant by lazy { FileAssistantWithDocuments(context) }
    private val mediaAssistant: MediaAssistant by lazy { MediaAssistantImpl(context) }
    private val codingAssistant: CodingAssistant by lazy { CodingAssistantImpl(context, secureStorage, null) }

    // Selection handling is now done via SelectionBroadcaster; legacy SelectionListener removed.

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
                        // Use AppResolver to find package
                        val pkg = AppResolver.resolvePackageForAppName(context, appQuery)
                        if (pkg != null) {
                            val launch = context.packageManager.getLaunchIntentForPackage(pkg)
                            if (launch != null) {
                                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(launch)
                                onResult(true, "Opened $appQuery")
                            } else {
                                onResult(false, "Cannot launch $appQuery")
                            }
                        } else {
                            onResult(false, "No app found matching $appQuery")
                        }
                    }
                })
                return true
            }
        }

        // WhatsApp specific commands
        if (lowered.contains("open whatsapp")) {
            requestConfirmation("Open WhatsApp", "Open WhatsApp?", {
                CoroutineScope(Dispatchers.Main).launch { WhatsAppHelper.openWhatsApp(context); onResult(true, "Opening WhatsApp") }
            })
            return true
        }

        if (lowered.contains("send this photo on whatsapp") || lowered.contains("send this photo on whatsapp") || lowered.contains("send this photo to whatsapp")) {
            requestConfirmation("Send photo", "Send the most recent photo via WhatsApp?", {
                CoroutineScope(Dispatchers.Main).launch {
                    val recent = mediaAssistant.searchMedia("").firstOrNull()
                    if (recent != null) {
                        WhatsAppHelper.sendImageToWhatsApp(context, recent)
                        onResult(true, "Sharing photo via WhatsApp")
                    } else {
                        onResult(false, "No recent photo found")
                    }
                }
            })
            return true
        }

        val sendMsgMatch = Regex("send message to (.+)").find(lowered)
        if (sendMsgMatch != null) {
            val contact = sendMsgMatch.groupValues[1].trim()
            requestConfirmation("Send WhatsApp message", "Send a WhatsApp message to $contact?", {
                CoroutineScope(Dispatchers.Main).launch {
                    if (!ContactResolver.hasContactsPermission(context)) {
                        onResult(false, "Missing READ_CONTACTS permission")
                        return@launch
                    }
                    // Find possible contacts
                    val matches = ContactSearch.findContactsByName(context, contact)
                    if (matches.isEmpty()) {
                        onResult(false, "Could not find contact $contact")
                        return@launch
                    }
                    if (matches.size == 1) {
                        val phone = matches.first().second
                        WhatsAppHelper.sendTextToContactViaApi(context, phone, "")
                        onResult(true, "Opened WhatsApp chat for $contact")
                        return@launch
                    }
                    // Multiple matches — delegate selection to UI by broadcasting matches and a continuation.
                    SelectionBroadcaster.notifyContactSelection(matches) { chosenPhone ->
                        // continuation invoked by UI when the user selects a phone
                        WhatsAppHelper.sendTextToContactViaApi(context, chosenPhone, "")
                    }
                    onResult(true, "Multiple contacts found for $contact — please select in UI")
                }
            })
            return true
        }

+        // Call commands: "call John", "call [contact name]"
+        val callMatch = Regex("call (.+)").find(lowered)
+        if (callMatch != null) {
+            val contact = callMatch.groupValues[1].trim()
+            requestConfirmation("Place call", "Call $contact?", {
+                CoroutineScope(Dispatchers.Main).launch {
+                    if (!ContactResolver.hasContactsPermission(context)) {
+                        onResult(false, "Missing READ_CONTACTS permission")
+                        return@launch
+                    }
+                    val matches = ContactSearch.findContactsByName(context, contact)
+                    if (matches.isEmpty()) {
+                        onResult(false, "Could not find contact $contact")
+                        return@launch
+                    }
+                    if (matches.size == 1) {
+                        val phone = matches.first().second
+                        CallHelper.placeCall(context, phone)
+                        onResult(true, "Dialing $contact")
+                        return@launch
+                    }
+                    // multiple matches: broadcast for UI selection; continuation will place call
+                    SelectionBroadcaster.notifyContactSelection(matches) { chosenPhone ->
+                        CallHelper.placeCall(context, chosenPhone)
+                    }
+                    onResult(true, "Multiple contacts found for $contact — please select in UI")
+                }
+            })
+            return true
+        }
+
+        // Open website commands: "open website example.com", "go to example.com", "open website" or "open site"
+        val openSiteMatch = Regex("open (website|site|web|browser) (.+)").find(lowered) ?: Regex("go to (.+)").find(lowered)
+        if (openSiteMatch != null) {
+            val site = openSiteMatch.groupValues.last().trim()
+            requestConfirmation("Open website", "Open website $site?", {
+                CoroutineScope(Dispatchers.Main).launch {
+                    try {
+                        val url = if (site.contains(".") || site.startsWith("http")) {
+                            if (site.startsWith("http")) site else "https://$site"
+                        } else {
+                            // perform a search
+                            "https://www.google.com/search?q=" + android.net.Uri.encode(site)
+                        }
+                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
+                        context.startActivity(intent)
+                        onResult(true, "Opening $site")
+                    } catch (e: Exception) {
+                        onResult(false, "Failed to open $site")
+                    }
+                }
+            })
+            return true
+        }
+
*** End Patch
