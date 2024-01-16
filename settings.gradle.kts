enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    includeBuild("build-logic")
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
buildCache {
    local {
        // Set local build cache directory.
        directory = "${settingsDir}/build-cache"
    }
}
rootProject.name = "TheWeChatMoments"
include(":app")
include(":designsystem")
