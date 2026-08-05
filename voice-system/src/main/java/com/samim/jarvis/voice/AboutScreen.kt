*** Begin Patch
*** Add File: voice-system/src/main/java/com/samim/jarvis/voice/AboutScreen.kt
+package com.samim.jarvis.voice
+
+import androidx.compose.foundation.layout.Column
+import androidx.compose.foundation.layout.fillMaxWidth
+import androidx.compose.material.Text
+import androidx.compose.runtime.Composable
+import androidx.compose.ui.Modifier
+
+@Composable
+fun AboutScreen() {
+    Column(modifier = Modifier.fillMaxWidth()) {
+        Text("App Name: SK SAMIM AI")
+        Text("Created By: Samim Boss")
+        Text("Creator Role: Founder and Developer of SK SAMIM AI")
+        Text("Location: Kolkata, India")
+        Text("Age: 18 (creator may choose to show this)")
+        Text("Education: College student")
+        Text("\nDescription: Samim Boss is the creator of SK SAMIM AI. He developed this assistant with the vision of creating a smart, modern and helpful AI companion.")
+    }
+}
+
*** End Patch
