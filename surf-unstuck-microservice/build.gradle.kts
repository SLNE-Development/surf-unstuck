import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.standalone")
    id("dev.slne.surf.microservice")
}

surfStandaloneApi  {
    withSurfDatabaseR2dbc("1.4.0", "dev.slne.surf.unstuck.libs")
}

dependencies {
    api(projects.surfUnstuckCore.surfUnstuckCoreCommon)
}

surfMicroservice {
    withMicroserviceApi()
    withRabbitModule(RabbitModule.SERVER_API, true)
}