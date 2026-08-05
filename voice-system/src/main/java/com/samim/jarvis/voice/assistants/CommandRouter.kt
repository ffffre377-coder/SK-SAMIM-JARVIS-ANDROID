*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/assistants/CommandRouter.kt
@@
-            requestConfirmation("Send WhatsApp message", "Send a WhatsApp message to $contact?", {
-                CoroutineScope(Dispatchers.Main).launch {
-                    if (!ContactResolver.hasContactsPermission(context)) {
-                        onResult(false, "Missing READ_CONTACTS permission")
-                        return@launch
-                    }
-                    val phone = ContactResolver.findPhoneNumberByName(context, contact)
-                    if (phone != null) {
-                        // For security, prompt user to speak the message or open WhatsApp compose via URL
-                        // Here we open WhatsApp chat with phone number and let the user type or paste
-                        WhatsAppHelper.sendTextToContactViaApi(context, phone, "")
-                        onResult(true, "Opened WhatsApp chat for $contact")
-                    } else {
-                        onResult(false, "Could not find contact $contact")
-                    }
-                }
-            })
+            requestConfirmation("Send WhatsApp message", "Send a WhatsApp message to $contact?", {
+                CoroutineScope(Dispatchers.Main).launch {
+                    if (!ContactResolver.hasContactsPermission(context)) {
+                        onResult(false, "Missing READ_CONTACTS permission")
+                        return@launch
+                    }
+                    // Find possible contacts
+                    val matches = ContactSearch.findContactsByName(context, contact)
+                    if (matches.isEmpty()) {
+                        onResult(false, "Could not find contact $contact")
+                        return@launch
+                    }
+                    if (matches.size == 1) {
+                        val phone = matches.first().second
+                        WhatsAppHelper.sendTextToContactViaApi(context, phone, "")
+                        onResult(true, "Opened WhatsApp chat for $contact")
+                        return@launch
+                    }
+                    // Multiple matches — request UI selection via stored callback in SecureStorage (the ViewModel/UI will read it)
+                    // For simplicity, save matches temporarily and signal UI to open selection (pragmatic approach)
+                    val tempKey = "contact_selection_temp"
+                    val payload = matches.joinToString(";;") { "${it.first}:::${it.second}" }
+                    com.samim.jarvis.security.SecureStorageProvider.get(context).putString(tempKey, payload)
+                    // Signal that UI should open selection - we rely on the hosting UI to poll or check this key
+                    onResult(true, "Multiple contacts found for $contact — please select in UI")
+                }
+            })
             return true
         }
*** End Patch
