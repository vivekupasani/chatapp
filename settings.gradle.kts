

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Add any additional repositories here if needed
        maven("https://jitpack.io")
        // Changed to use https for Glide snapshots

    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add any additional repositories here if needed
        maven("https://jitpack.io")
    }
}

rootProject.name = "ChatApp"
include(":app")
