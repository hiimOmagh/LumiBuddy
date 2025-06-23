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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LumiBuddy"
include(":app")
include(":app:core-infra")
include(":feature_measurement")
include(":core_data")
include(":core-domain")
include(":core-data")
include(":core-infra")
include(":feature-measurement")
include(":feature-plantdb")
include(":feature-diary")
include(":feature-growschedule")
include(":feature-ar")
