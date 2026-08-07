plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.samim.jarvis.ai"
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
    implementation(project(":api-manager"))
    implementation(project(":memory"))
    implementation(project(":security"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
