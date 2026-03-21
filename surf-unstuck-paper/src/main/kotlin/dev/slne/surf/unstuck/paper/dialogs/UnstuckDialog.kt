@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.unstuck.paper.dialogs

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.core.api.paper.util.toSurfPlayer
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickCallback
import dev.slne.surf.unstuck.core.service.unstuckService
import dev.slne.surf.unstuck.core.usage.UnstuckUsage
import dev.slne.surf.unstuck.core.util.WorldLocation
import dev.slne.surf.unstuck.paper.commands.usedUnstuckCache
import dev.slne.surf.unstuck.paper.permission.PermissionRegistry
import dev.slne.surf.unstuck.paper.plugin
import dev.slne.surf.unstuck.paper.utils.canBuildAtOwnLocation
import io.papermc.paper.registry.data.dialog.DialogBase
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import java.time.ZonedDateTime

fun createUnstuckDialog() = dialog {
    base {
        title { primary("Unstuck") }
        afterAction(DialogBase.DialogAfterAction.WAIT_FOR_RESPONSE)

        body {
            plainMessage(400) {
                info("Du steckst auf einem Grundstück fest?")
                appendNewline(2)

                info("Bitte beachte folgende Informationen:")
                appendNewline()

                error("Der Befehl darf nur verwendet werden, wenn du auch tatsächlich feststeckst")
                appendSpace()
                error("Der Missbrauch des Befehls wird als Exploiting gewertet und dementsprechend geahndet")
                appendNewline(2)

                info("Möchtest du dich wirklich zum Spawn teleportieren lassen?")
            }
        }
    }

    type {
        confirmation(confirmAction(), cancelAction())
    }
}

private fun createNotice(usage: UnstuckUsage) = dialog {
    val result = usage.result?.toResult()

    base {
        title { primary("Unstuck") }
        afterAction(DialogBase.DialogAfterAction.NONE)

        body {
            plainMessage(400) {
                if (result != null) {
                    append(result)
                }
            }
        }
    }

    type {
        notice {
            label { text("Schließen") }
            tooltip { info("Klicke, um das Fenster zu schließen") }

            action {
                playerCallback {
                    it.clearDialogs()
                }
            }
        }
    }
}

private fun confirmAction() = actionButton {
    label { text("Bestätigen") }
    tooltip { info("Klicke, um den Vorgang zu bestätigen") }

    action {
        playerCallback { player ->
            plugin.launch {
                val cloudPlayer = player.toSurfPlayer()
                val server = cloudPlayer.currentServer?.name ?: "unknown"

                val location = player.location
                val spawnLocation = player.world.spawnLocation

                val usage = UnstuckUsage(
                    uuid = player.uniqueId,
                    executedAt = ZonedDateTime.now(),
                    server = server,
                    location = WorldLocation(
                        worldUuid = location.world.uid,
                        x = location.x,
                        y = location.y,
                        z = location.z
                    )
                )

                if (player.canBuildAtOwnLocation()) {
                    logWithResult(player, usage, UnstuckUsage.DbResult.FAILED_CAN_BUILD)
                    return@launch
                }

                withContext(plugin.regionDispatcher(spawnLocation)) {
                    player.teleportAsync(spawnLocation)
                    logWithResult(player, usage, UnstuckUsage.DbResult.SUCCESS)

                    usedUnstuckCache.put(player.uniqueId, ZonedDateTime.now())
                }
            }
        }
    }
}

private fun logWithResult(player: Player, usage: UnstuckUsage, result: UnstuckUsage.DbResult) {
    usage.result = result

    plugin.launch {
        unstuckService.createUsage(usage)
    }

    Bukkit.broadcast(buildText {
        if(result == UnstuckUsage.DbResult.SUCCESS) {
            appendWarningPrefix()
            info("Der Spieler ")
            variableValue(player.name)
            info(" hat den Unstuck-Befehl verwendet und wurde zum Spawn teleportiert.")
        } else {
            appendWarningPrefix()
            info("Der Spieler ")
            variableValue(player.name)
            info(" hat versucht, den Unstuck-Befehl zu verwenden, obwohl er nicht feststeckt.")
        }
        clickCallback {
            val staff = it as? Player ?: return@clickCallback
            staff.teleportAsync(Location(Bukkit.getWorld(usage.location.worldUuid) ?: return@clickCallback,
                usage.location.x,
                usage.location.y,
                usage.location.z))
        }
    }, PermissionRegistry.ALERT)

    player.showDialog(createNotice(usage))
}

private fun cancelAction() = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Klicke, um den Vorgang abzubrechen") }

    action {
        playerCallback { player ->
            player.clearDialogs(true)
        }
    }
}