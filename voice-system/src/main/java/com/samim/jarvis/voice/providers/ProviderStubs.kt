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
        StubRemoteProvider("Coqui", repo),
        AndroidTtsProvider(/** caller should pass TextToSpeechManager when used with DI */ TODO())
    )
}
