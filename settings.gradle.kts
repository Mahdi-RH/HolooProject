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
        // Neshan maven repo
        maven("https://maven.neshan.org/artifactory/public-maven")
    }
}

rootProject.name = "HolooMapProject"
include(":app")
