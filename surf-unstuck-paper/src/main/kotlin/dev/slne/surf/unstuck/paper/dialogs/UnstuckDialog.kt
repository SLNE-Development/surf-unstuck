@file:Suppress("UnstableApiUsage")
@file:OptIn(NmsUseWithCaution::class)

package dev.slne.surf.unstuck.paper.dialogs

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.cloud.api.common.player.teleport.TeleportLocation
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.surfapi.bukkit.api.dialog.base
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import dev.slne.surf.surfapi.bukkit.api.dialog.clearDialogs
import dev.slne.surf.surfapi.bukkit.api.dialog.dialog
import dev.slne.surf.surfapi.bukkit.api.dialog.type
import dev.slne.surf.surfapi.bukkit.api.nms.NmsUseWithCaution
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.unstuck.core.common.UnstuckUsage
import dev.slne.surf.unstuck.core.common.netty.protocol.serverbound.ServerboundCreateUnstuckUsagePacket
import dev.slne.surf.unstuck.paper.commands.usedUnstuckCache
import dev.slne.surf.unstuck.paper.plugin
import dev.slne.surf.unstuck.paper.utils.canBuildAtOwnLocation
import io.papermc.paper.registry.data.dialog.DialogBase
import kotlinx.coroutines.withContext
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
                val cloudPlayer = player.toCloudPlayer()
                    ?: error("No cloud player found for player ${player.uniqueId} in surf-unstuck usage. This should never happen.")

                val server = cloudPlayer.currentServer()

                val location = player.location
                val spawnLocation = player.world.spawnLocation

                val usage = UnstuckUsage(
                    uuid = player.uniqueId,
                    executedAt = ZonedDateTime.now(),
                    server = server.name,
                    location = TeleportLocation(
                        world = location.world.uid,
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
    ServerboundCreateUnstuckUsagePacket(usage).fireAndForget()

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