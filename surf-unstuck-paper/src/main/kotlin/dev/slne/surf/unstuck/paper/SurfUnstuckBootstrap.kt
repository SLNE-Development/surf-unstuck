@file:Suppress("UnstableApiUsage")

package dev.slne.surf.unstuck.paper

import dev.slne.surf.cloud.api.common.CloudInstance
import dev.slne.surf.cloud.api.common.startSpringApplication
import dev.slne.surf.unstuck.SurfUnstuckApplication
import dev.slne.surf.unstuck.core.common.ContextHolder
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap

class SurfUnstuckBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        ContextHolder.context = CloudInstance.startSpringApplication(SurfUnstuckApplication::class)
    }
}