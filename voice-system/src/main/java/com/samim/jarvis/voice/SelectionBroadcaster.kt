package com.samim.jarvis.voice

/**
 * SelectionBroadcaster: global in-process broadcaster for pending selection flows.
 * Listeners register to receive selection requests (contacts/files/apps) along with a continuation
 * that should be invoked with the chosen item.
 */
object SelectionBroadcaster {
    private var contactListener: ((List<Pair<String, String>>, (String) -> Unit) -> Unit)? = null
    private var fileListener: ((List<Pair<String, android.net.Uri>>, (android.net.Uri) -> Unit) -> Unit)? = null
    private var appListener: ((List<Pair<String, String>>, (String) -> Unit) -> Unit)? = null

    fun registerContactListener(l: (List<Pair<String, String>>, (String) -> Unit) -> Unit) {
        contactListener = l
    }

    fun unregisterContactListener() {
        contactListener = null
    }

    fun notifyContactSelection(matches: List<Pair<String, String>>, onChosen: (String) -> Unit) {
        contactListener?.invoke(matches, onChosen)
    }

    fun registerFileListener(l: (List<Pair<String, android.net.Uri>>, (android.net.Uri) -> Unit) -> Unit) {
        fileListener = l
    }

    fun unregisterFileListener() {
        fileListener = null
    }

    fun notifyFileSelection(matches: List<Pair<String, android.net.Uri>>, onChosen: (android.net.Uri) -> Unit) {
        fileListener?.invoke(matches, onChosen)
    }

    fun registerAppListener(l: (List<Pair<String, String>>, (String) -> Unit) -> Unit) {
        appListener = l
    }

    fun unregisterAppListener() {
        appListener = null
    }

    fun notifyAppSelection(matches: List<Pair<String, String>>, onChosen: (String) -> Unit) {
        appListener?.invoke(matches, onChosen)
    }
}
