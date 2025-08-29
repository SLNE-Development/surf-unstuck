package dev.slne.surf.unstuck.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import org.bukkit.plugin.java.JavaPlugin

class SurfUnstuck : SuspendingJavaPlugin() {
}

val plugin get() = JavaPlugin.getPlugin(SurfUnstuck::class.java)