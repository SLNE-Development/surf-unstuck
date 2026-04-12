package dev.slne.surf.unstuck.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.unstuck.core.client.ClientUnstuckInstance
import dev.slne.surf.unstuck.paper.commands.unstuckCommand
import dev.slne.surf.unstuck.paper.listener.PortalListener
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {

    override suspend fun onLoadAsync() {
        ClientUnstuckInstance.clientLoader.onLoad()
    }

    override suspend fun onEnableAsync() {
        PortalListener.register()
        unstuckCommand()
    }

    override suspend fun onDisableAsync() {
        ClientUnstuckInstance.clientLoader.onDisable()
    }
}