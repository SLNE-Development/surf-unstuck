pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}

rootProject.name = "surf-unstuck"

include("surf-unstuck-core")
include("surf-unstuck-microservice")
include("surf-unstuck-paper")
include("surf-unstuck-core:surf-unstuck-core-common")
include("surf-unstuck-core:surf-unstuck-core-client")