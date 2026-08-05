apply plugin: 'com.android.library'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-kapt'

android {
    namespace = "com.samim.jarvis.voice"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Porcupine (Picovoice) wake-word engine integration (OPTIONAL):
    // To enable wake-word detection, add the Porcupine Android dependency and the native models.
    // Example (add and adjust version as needed):
    // implementation "ai.picovoice:porcupine-android:YourPorcupineVersion"
    // NOTE: Porcupine requires model and keyword files and its own license terms. We keep the SDK optional and a scaffold in PorcupineManager.
}
