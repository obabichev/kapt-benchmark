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
