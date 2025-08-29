package dev.slne.surf.unstuck.server

import dev.slne.surf.cloud.api.common.CloudInstance
import dev.slne.surf.cloud.api.common.startSpringApplication
import dev.slne.surf.cloud.api.server.plugin.bootstrap.BootstrapContext
import dev.slne.surf.cloud.api.server.plugin.bootstrap.StandalonePluginBootstrap
import dev.slne.surf.unstuck.SurfUnstuckApplication
import dev.slne.surf.unstuck.core.common.ContextHolder

class PluginBootstrap : StandalonePluginBootstrap {
    override suspend fun bootstrap(context: BootstrapContext) {
        ContextHolder.context = CloudInstance.startSpringApplication(SurfUnstuckApplication::class)
    }
}