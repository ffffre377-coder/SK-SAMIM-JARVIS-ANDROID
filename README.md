# SK-SAMIM-JARVIS-ANDROID

Kotlin + Jetpack Compose multi-module scaffold for the SK-SAMIM JARVIS Android project.

This is an initial lightweight scaffold intended for low-end Android devices and future expansion into a full JARVIS AI OS.

Modules included:
- :app — Android application module (Compose UI)
- :ai-engine — core AI orchestration & adapters
- :voice-system — wake-word, STT, TTS, voice assistant glue
- :api-manager — provider adapters for Gemini, OpenAI, Claude, DeepSeek, etc.
- :memory — Room database for conversations & messages
- :security — secure storage & permission helpers
- :camera-vision — CameraX + OCR placeholder
- :file-manager — file browsing helpers
- :phone-control — device control helpers

Features included in scaffold:
- Hilt DI wiring
- Room entities/DAO + Database
- Retrofit + OkHttp skeleton
- EncryptedSharedPreferences helper for secure API keys
- Permission manager scaffolding
- Basic Compose screens: Chat, Voice Assistant, Settings

Getting started
1. Clone the repo:
   git clone https://github.com/ffffre377-coder/SK-SAMIM-JARVIS-ANDROID.git
2. Open in Android Studio (Arctic Fox or newer recommended) or build from command line with Gradle wrapper.
3. Add API keys in Settings (secure storage). Configure providers in app settings.

Notes
- This is a scaffold. Provider integrations (Gemini/OpenAI etc.) are placeholders and must be completed with credentials and API implementations.
- For local/offline models, consider integrating quantized runtimes in :ai-engine.

License: MIT
