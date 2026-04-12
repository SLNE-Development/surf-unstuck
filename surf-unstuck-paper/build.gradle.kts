import dev.slne.surf.api.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.unstuck.paper.PaperMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    withCorePaper()

    serverDependencies {
        registerRequired("WorldGuard")
    }
}

dependencies {
    api(projects.surfUnstuckCore.surfUnstuckCoreClient)

    compileOnly(libs.worldguard) {
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "com.google.code.gson", module = "gson")
        exclude(group = "it.unimi.dsi", module = "fastutil")
    }
}