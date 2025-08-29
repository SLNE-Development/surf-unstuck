package dev.slne.surf.unstuck.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.github.shynixn.mccoroutine.folia.ticks
import dev.slne.surf.cloud.api.common.util.mutableObject2ObjectMapOf
import dev.slne.surf.cloud.api.common.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.unstuck.paper.plugin
import dev.slne.surf.unstuck.paper.utils.setOfflineLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*
import kotlin.time.Duration.Companion.seconds

object PortalListener : Listener {

    private val BACK_DURATION = 10.seconds

    private val locations = mutableObject2ObjectMapOf<UUID, Location>()
    private val jobs = mutableObject2ObjectMapOf<UUID, Job>()

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val oldLocation = event.from

        if (event.cause != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            locations.remove(uuid)
            cancelJob(player)

            return
        }

        locations[uuid] = oldLocation
        cancelJob(player)

        val startTime = System.currentTimeMillis()
        val job = plugin.launch {
            while (isActive) {
                if (!isInPortal(player)) {
                    cancelJob(player)
                    return@launch
                }

                if (System.currentTimeMillis() - startTime >= BACK_DURATION.inWholeMilliseconds) {
                    withContext(plugin.regionDispatcher(oldLocation)) {
                        player.teleportAsync(oldLocation)

                        player.sendText {
                            appendPrefix()

                            error("Du hast das Portal nicht rechtzeitig verlassen und wurdest zu deiner letzten Position zurückgebracht.")
                        }

                        locations.remove(uuid)
                        cancelJob(player)
                    }
                }

                delay(1.seconds)
            }
        }

        jobs[uuid] = job
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        plugin.launch {
            delay(1.ticks)

            setLocation(player)
            cancelJob(player)
        }
    }

    private fun cancelJob(player: Player) {
        val uuid = player.uniqueId

        val job = jobs.remove(uuid) ?: return

        if (job.isActive) {
            job.cancel()
        }
    }

    private suspend fun setLocation(player: Player): Boolean {
        val uuid = player.uniqueId

        if (!isInPortal(player)) {
            locations.remove(uuid)

            return false
        }

        val location = locations.remove(uuid) ?: return false

        return runCatching {
            player.setOfflineLocation(location)
            true
        }.getOrNull() ?: false
    }

    private fun isInPortal(player: Player): Boolean {
        val location = player.location
        val block = location.block

        val relatives = mutableObjectListOf(
            block.type,
            block.getRelative(BlockFace.UP, 1).type
        )

        if (relatives[0] == Material.AIR && relatives[1] == Material.AIR) {
            return false
        }

        relatives.add(block.getRelative(BlockFace.NORTH, 1).type)
        relatives.add(block.getRelative(BlockFace.EAST, 1).type)
        relatives.add(block.getRelative(BlockFace.SOUTH, 1).type)
        relatives.add(block.getRelative(BlockFace.WEST, 1).type)

        return relatives.contains(Material.NETHER_PORTAL)
    }

}