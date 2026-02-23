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
        maven{url = uri("https://maven.myket.ir")}
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
      
        maven{url = uri("https://maven.myket.ir")}
    }
}

rootProject.name = "YarAnbar"
include(":app")
include(":login")
