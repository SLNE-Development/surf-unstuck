plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "surf-unstuck"

include("surf-unstuck-core:surf-unstuck-core-common")
include("surf-unstuck-server")
include("surf-unstuck-paper")