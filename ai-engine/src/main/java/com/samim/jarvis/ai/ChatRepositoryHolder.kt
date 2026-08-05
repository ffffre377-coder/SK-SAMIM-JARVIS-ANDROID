package com.samim.jarvis.ai

/**
 * Simple holder to expose a globally available ChatRepository instance to parts of the app
 * that cannot receive it by DI. This is a pragmatic bridge — prefer DI in future.
 */
object ChatRepositoryHolder {
    @Volatile
    var instance: ChatRepository? = null

    fun register(repo: ChatRepository) {
        instance = repo
    }

    fun unregister() {
        instance = null
    }
}
