// settings.gradle.kts
pluginManagement {
    repositories {
        if (System.getenv("CI") == null) {
            maven { url = uri("https://maven.myket.ir") }
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        if (System.getenv("CI") == null) {
            maven { url = uri("https://maven.myket.ir") }
        }
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
            content { includeGroupByRegex("com\\.github.*") }
        }
    }
}

rootProject.name = "YarAnbar"
include(":app")
include(":login")