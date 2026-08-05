*** Begin Patch
*** Update File: voice-system/src/main/java/com/samim/jarvis/voice/assistants/CommandRouter.kt
@@
-    interface SelectionListener {
-        fun onContactSelection(matches: List<Pair<String, String>>)
-        fun onAppSelection(matches: List<Pair<String, String>>)
-        fun onFileSelection(matches: List<Pair<String, String>>)
-    }
-
-    private var selectionListener: SelectionListener? = null
-
-    fun setSelectionListener(l: SelectionListener?) {
-        selectionListener = l
-    }
+    // Selection handling is now done via SelectionBroadcaster; legacy SelectionListener removed.
*** End Patch
