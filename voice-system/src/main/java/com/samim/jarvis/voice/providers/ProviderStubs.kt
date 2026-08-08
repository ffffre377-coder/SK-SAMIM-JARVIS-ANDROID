package com.samim.jarvis.voice.providers

import com.samim.jarvis.voice.TtsProvider

// Register provider stubs for DI or manual registration. These are helper functions to create stubs when needed.
fun createDefaultStubs(repo: VoiceProviderRepository): List<TtsProvider> {
    return listOf(
        StubRemoteProvider("ElevenLabs", repo),
        StubRemoteProvider("OpenAI", repo),
        StubRemoteProvider("GoogleCloudTTS", repo),
        StubRemoteProvider("AzureAI", repo),
        StubRemoteProvider("AmazonPolly", repo),
        StubRemoteProvider("Cartesia", repo),
        StubRemoteProvider("PlayHT", repo),
        StubRemoteProvider("Deepgram", repo),
        StubRemoteProvider("Coqui", repo)
        // AndroidTtsProvider omitted here because it requires a TextToSpeechManager instance which
        // cannot be created from this helper without a Context. If you want AndroidTtsProvider
        // in the default list, change this helper to accept a TextToSpeechManager parameter.
    )
}
