package dev.slne.surf.unstuck.server.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.OfflineCloudPlayer
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServerManager
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.unstuck.core.common.UnstuckUsage
import dev.slne.surf.unstuck.core.common.netty.protocol.serverbound.ServerboundCreateUnstuckUsagePacket
import dev.slne.surf.unstuck.server.UnstuckService
import kotlinx.coroutines.*
import net.kyori.adventure.text.event.ClickEvent
import org.springframework.stereotype.Component

@Component
class UnstuckUsageListener(
    private val unstuckService: UnstuckService
) {

    private val log = logger()
    private val scope =
        CoroutineScope(SupervisorJob() + CoroutineName("UnstuckUsageListener") + CoroutineExceptionHandler { context, throwable ->
            log.atSevere()
                .withCause(throwable)
                .log("Exception in coroutine %s", context[CoroutineName]?.name ?: "unknown")
        })

    @SurfNettyPacketHandler
    suspend fun handleCreateUsage(packet: ServerboundCreateUnstuckUsagePacket) {
        val usage = packet.unstuckUsage

        unstuckService.createUsage(usage)

        val server = CloudServerManager.retrieveServerByName(usage.server)
        val cloudPlayer = usage.cloudPlayer

        CloudServerManager.broadcast(buildText {
            appendPrefix()

            variableValue(cloudPlayer.name() ?: cloudPlayer.uuid.toString())
            info(" hat Unstuck genutzt auf ")
            variableValue(server?.name ?: usage.server)
            spacer("(")
            variableValue(usage.result?.name ?: "UNKNOWN")
            spacer(")")

            if (server != null) {
                appendSpace()
                append {
                    hoverEvent(buildText {
                        info("Klicke, um dich zu teleportieren")
                    })

                    clickEvent(ClickEvent.callback { audience ->
                        val audiencePlayer = audience.toCloudPlayer() ?: return@callback

                        teleportToUsage(audiencePlayer, cloudPlayer, usage)
                    })
                }
            }
        }, "surf.unstuck.notify", true)
    }

    private fun teleportToUsage(
        cloudPlayer: CloudPlayer,
        otherPlayer: OfflineCloudPlayer,
        usage: UnstuckUsage
    ) = scope.launch {
        val result = cloudPlayer.connectToServer(usage.server)

        if (!result.isSuccess) {
            cloudPlayer.sendText {
                appendPrefix()
                info("Fehler beim Verbinden zu ")
                variableValue(usage.server)
                info(": ")
                append(result.message)
            }

            return@launch
        }

        cloudPlayer.teleport(usage.location)

        cloudPlayer.sendText {
            appendPrefix()
            info("Du wurdest zu ")
            variableValue(
                otherPlayer.name() ?: otherPlayer.uuid.toString()
            )
            info(" teleportiert")
        }
    }
}