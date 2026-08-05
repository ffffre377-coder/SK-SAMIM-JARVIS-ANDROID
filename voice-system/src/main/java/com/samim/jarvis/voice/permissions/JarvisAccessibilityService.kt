package com.samim.jarvis.voice

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Accessibility service stub with a global instance pointer for safe in-process usage.
 * The service does not perform any actions automatically. Use this to power optional
 * screen-understanding features when the user enables them explicitly in settings.
 */
class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: JarvisAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("JarvisA11y", "service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Intentionally empty: we do not auto-act on accessibility events.
    }

    override fun onInterrupt() {
        Log.d("JarvisA11y", "interrupted")
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    /**
     * Provide a simple, best-effort textual description of the current screen using the
     * AccessibilityNodeInfo tree. This is only intended to assist confirmation dialogs
     * and debugging; it should NOT expose sensitive data or be used to bypass protections.
     */
    fun describeCurrentScreen(): String {
        try {
            val root: AccessibilityNodeInfo? = rootInActiveWindow
            if (root == null) return "No accessible content on screen"
            var clickableCount = 0
            var textCount = 0
            val builder = StringBuilder()
            val queue = ArrayDeque<AccessibilityNodeInfo>()
            queue.add(root)
            while (queue.isNotEmpty()) {
                val node = queue.removeFirst()
                try {
                    if (node.className != null && node.isClickable) clickableCount++
                    val txt = node.text
                    if (!txt.isNullOrBlank()) {
                        textCount++
                        if (builder.length < 800) builder.append("\n• ").append(txt)
                    }
                    for (i in 0 until node.childCount) {
                        node.getChild(i)?.let { queue.add(it) }
                    }
                } catch (e: Exception) {
                    // ignore node traversal errors
                } finally {
                    // recycle node if possible
                    try { node.recycle() } catch (_: Exception) {}
                }
            }
            return "Screen summary: $clickableCount clickable elements, $textCount text nodes." + builder.toString()
        } catch (e: Exception) {
            Log.e("JarvisA11y", "describe screen failed", e)
            return "Unable to describe screen"
        }
    }
}
