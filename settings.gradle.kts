// Plugin repositories for build plugins
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.13.0"
        id("org.jetbrains.kotlin.android") version "2.0.0"
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
        id("com.google.dagger.hilt.android") version "2.51.1"
        id("com.google.gms.google-services") version "4.4.1"
        id("com.google.devtools.ksp") version "2.0.0-1.0.21"
    }
}

// Add repositories for all project dependencies
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Cloudinary repository
        maven { 
            url = uri("https://artifactory.cloudinary.com/artifactory/libs-release") 
            content {
                includeGroup("com.cloudinary")
            }
        }
    }
}

rootProject.name = "FarmerMP"
include(":app")