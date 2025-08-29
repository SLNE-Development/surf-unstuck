plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    withCloudClientPaper()
    mainClass("dev.slne.surf.unstuck.paper.SurfUnstuck")
    bootstrapper("dev.slne.surf.unstuck.paper.SurfUnstuckBootstrap")
}

dependencies {
    api(project(":surf-unstuck-core:surf-unstuck-core-common"))
}