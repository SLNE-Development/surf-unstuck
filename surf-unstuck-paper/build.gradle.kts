import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    withCloudClientPaper()
    mainClass("dev.slne.surf.unstuck.paper.SurfUnstuck")
    bootstrapper("dev.slne.surf.unstuck.paper.SurfUnstuckBootstrap")
    foliaSupported(true)
    generateLibraryLoader(false)

    serverDependencies {
        registerRequired("WorldGuard")
        registerRequired("surf-cloud-bukkit")
    }

    bootstrapDependencies {
        registerRequired("surf-cloud-bukkit")
    }
}

dependencies {
    api(project(":surf-unstuck-core:surf-unstuck-core-common"))

    compileOnly(libs.worldguard) {
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "it.unimi.dsi", module = "fastutil")
    }
}