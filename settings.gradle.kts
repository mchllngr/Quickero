dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // TODO test whats really needed
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // for beagle
    }
}

include(":app")

enableFeaturePreview("VERSION_CATALOGS")
