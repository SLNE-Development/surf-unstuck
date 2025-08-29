package dev.slne.surf.unstuck.server

import dev.slne.surf.cloud.api.server.plugin.StandalonePlugin

class PluginMain : StandalonePlugin() {
    override suspend fun load() {
    }

    override suspend fun enable() {
    }

    override suspend fun disable() {
    }
}

val plugin get() = StandalonePlugin.getPlugin(PluginMain::class)