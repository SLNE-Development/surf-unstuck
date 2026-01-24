package dev.slne.surf.unstuck.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.unstuck.core.database.databaseLoader
import dev.slne.surf.unstuck.paper.commands.unstuckCommand
import dev.slne.surf.unstuck.paper.listener.PortalListener
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        PortalListener.register()
        unstuckCommand()

        databaseLoader.connect(plugin.dataPath)
    }

    override fun onDisable() {
        databaseLoader.disconnect()
    }
}