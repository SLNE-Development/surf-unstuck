package dev.slne.surf.unstuck.paper.utils

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.LocalPlayer
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import org.bukkit.Location
import org.bukkit.entity.Player
import com.sk89q.worldedit.util.Location as WorldGuardLocation

private val regionContainer by lazy {
    WorldGuard.getInstance().platform.regionContainer
}

private val query by lazy {
    regionContainer.createQuery()
}

fun Player.wrapAsLocalPlayer(): LocalPlayer? = WorldGuardPlugin.inst().wrapPlayer(this)
fun Location.adaptToWorldGuard(): WorldGuardLocation? = BukkitAdapter.adapt(this)

fun Player.canBuildAtOwnLocation() =
    query.testBuild(location.adaptToWorldGuard(), wrapAsLocalPlayer())