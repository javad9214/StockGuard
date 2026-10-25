// settings.gradle.kts
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
        if (System.getenv("CI") == null) {
            maven { url = uri("https://maven.myket.ir") }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        if (System.getenv("CI") == null) {
            // Local (Iran): mirror only, as in your working setup
            maven { url = uri("https://maven.myket.ir") }
        } else {
            // GitHub Actions: official repos
            google()
            mavenCentral()
            maven {
                url = uri("https://jitpack.io")
                content { includeGroupByRegex("com\\.github.*") }
            }
        }
    }
}

rootProject.name = "YarAnbar"
include(":app")
include(":login")