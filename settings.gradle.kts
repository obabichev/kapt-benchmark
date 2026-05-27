rootProject.name = "kapt-benchmark"

pluginManagement {
    val kotlinVersion: String = settings.providers.gradleProperty("kotlin.version").get()
    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("kapt") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

include(
    ":source-generator",
    ":synthetic-processor",
    ":samples:synthetic-noop",
    ":samples:dagger-sample",
    ":samples:mapstruct-sample",
)
