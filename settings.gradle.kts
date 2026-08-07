pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SK-SAMIM-JARVIS-ANDROID"

include(
    ":app",
    ":ai-engine",
    ":voice-system",
    ":api-manager",
    ":memory",
    ":security",
    ":camera-vision",
    ":file-manager",
    ":phone-control"
)
