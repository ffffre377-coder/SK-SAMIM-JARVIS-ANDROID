*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/assistants/CommandRouter.kt
@@
         // Smart search: apps
         if (lowered.contains("open settings") || lowered.contains("find app") || lowered.contains("open")) {
             val appQuery = extractQuery(lowered, listOf("open", "find app", "open settings"))
             if (!appQuery.isNullOrBlank()) {
                 requestConfirmation("Open app", "Open app matching '$appQuery'?", {
                     CoroutineScope(Dispatchers.Main).launch {
-                        val pm = context.packageManager
-                        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
-                        val match = apps.firstOrNull { it.loadLabel(pm).toString().lowercase().contains(appQuery) }
-                        if (match != null) {
-                            val launch = pm.getLaunchIntentForPackage(match.packageName)
-                            if (launch != null) {
-                                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
-                                context.startActivity(launch)
-                                onResult(true, "Opened ${match.loadLabel(pm)}")
-                            } else {
-                                onResult(false, "Cannot launch ${match.loadLabel(pm)}")
-                            }
-                        } else {
-                            onResult(false, "No app found matching $appQuery")
-                        }
+                        // Use AppResolver to find package
+                        val pkg = AppResolver.resolvePackageForAppName(context, appQuery)
+                        if (pkg != null) {
+                            val launch = context.packageManager.getLaunchIntentForPackage(pkg)
+                            if (launch != null) {
+                                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
+                                context.startActivity(launch)
+                                onResult(true, "Opened $appQuery")
+                            } else {
+                                onResult(false, "Cannot launch $appQuery")
+                            }
+                        } else {
+                            onResult(false, "No app found matching $appQuery")
+                        }
                     }
                 })
                 return true
             }
         }
+
+        // WhatsApp specific commands
+        if (lowered.contains("open whatsapp")) {
+            requestConfirmation("Open WhatsApp", "Open WhatsApp?", {
+                CoroutineScope(Dispatchers.Main).launch { WhatsAppHelper.openWhatsApp(context); onResult(true, "Opening WhatsApp") }
+            })
+            return true
+        }
+
+        if (lowered.contains("send this photo on whatsapp") || lowered.contains("send this photo on whatsapp") || lowered.contains("send this photo to whatsapp")) {
+            requestConfirmation("Send photo", "Send the most recent photo via WhatsApp?", {
+                CoroutineScope(Dispatchers.Main).launch {
+                    val recent = mediaAssistant.searchMedia("").firstOrNull()
+                    if (recent != null) {
+                        WhatsAppHelper.sendImageToWhatsApp(context, recent)
+                        onResult(true, "Sharing photo via WhatsApp")
+                    } else {
+                        onResult(false, "No recent photo found")
+                    }
+                }
+            })
+            return true
+        }
+
+        val sendMsgMatch = Regex("send message to (.+)").find(lowered)
+        if (sendMsgMatch != null) {
+            val contact = sendMsgMatch.groupValues[1].trim()
+            requestConfirmation("Send WhatsApp message", "Send a WhatsApp message to $contact?", {
+                CoroutineScope(Dispatchers.Main).launch {
+                    if (!ContactResolver.hasContactsPermission(context)) {
+                        onResult(false, "Missing READ_CONTACTS permission")
+                        return@launch
+                    }
+                    val phone = ContactResolver.findPhoneNumberByName(context, contact)
+                    if (phone != null) {
+                        // For security, prompt user to speak the message or open WhatsApp compose via URL
+                        // Here we open WhatsApp chat with phone number and let the user type or paste
+                        WhatsAppHelper.sendTextToContactViaApi(context, phone, "")
+                        onResult(true, "Opened WhatsApp chat for $contact")
+                    } else {
+                        onResult(false, "Could not find contact $contact")
+                    }
+                }
+            })
+            return true
+        }
*** End Patch
