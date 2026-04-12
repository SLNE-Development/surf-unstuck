@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.unstuck.paper.dialogs

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.api.core.messages.adventure.appendNewline
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.paper.dialog.base
import dev.slne.surf.api.paper.dialog.builder.actionButton
import dev.slne.surf.api.paper.dialog.clearDialogs
import dev.slne.surf.api.paper.dialog.dialog
import dev.slne.surf.api.paper.dialog.type
import dev.slne.surf.api.paper.nms.NmsUseWithCaution
import dev.slne.surf.core.api.paper.util.toSurfPlayer
import dev.slne.surf.unstuck.core.client.service.UnstuckService
import dev.slne.surf.unstuck.core.common.usage.UnstuckUsage
import dev.slne.surf.unstuck.core.common.util.WorldLocation
import dev.slne.surf.unstuck.paper.commands.usedUnstuckCache
import dev.slne.surf.unstuck.paper.permission.PermissionRegistry
import dev.slne.surf.unstuck.paper.plugin
import dev.slne.surf.unstuck.paper.utils.canBuildAtOwnLocation
import io.papermc.paper.registry.data.dialog.DialogBase
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
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
        UnstuckService.createUsage(usage)
    }

    val x = usage.location.x.round1()
    val y = usage.location.y.round1()
    val z = usage.location.z.round1()
    val worldName = Bukkit.getWorld(usage.location.worldUuid)?.name

    Bukkit.broadcast(buildText {
        if (result == UnstuckUsage.DbResult.SUCCESS) {
            appendWarningPrefix()
            info("Der Spieler ")
            variableValue(player.name)
            info(" hat den Unstuck-Befehl verwendet und wurde zum Spawn teleportiert. ")
            info("Koordinaten: ")
            variableValue("$x, $y, $z")
        } else {
            appendWarningPrefix()
            info("Der Spieler ")
            variableValue(player.name)
            info(" hat versucht, den Unstuck-Befehl zu verwenden, obwohl er nicht feststeckt. ")
            info("Koordinaten: ")
            variableValue("$x, $y, $z")
        }
        hoverEvent(buildText {
            spacer("- ")
            info("Ort: ")
            variableValue("$x, $y, $z in Welt $worldName")
            appendNewline(2)
            spacer("Klicke, um dich zu teleportieren.")
        })
        clickEvent(ClickEvent.callback {
            val staff = it as? Player ?: return@callback
            staff.teleportAsync(
                Location(
                    Bukkit.getWorld(usage.location.worldUuid) ?: return@callback,
                    usage.location.x,
                    usage.location.y,
                    usage.location.z
                )
            )
        })
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

private fun Double.round1(): Double = kotlin.math.round(this * 10) / 10