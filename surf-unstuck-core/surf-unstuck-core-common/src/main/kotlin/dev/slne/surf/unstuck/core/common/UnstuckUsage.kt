package dev.slne.surf.unstuck.core.common

import dev.slne.surf.cloud.api.common.netty.network.codec.streamCodec
import dev.slne.surf.cloud.api.common.netty.protocol.buffer.SurfByteBuf
import dev.slne.surf.cloud.api.common.netty.protocol.buffer.readEnum
import dev.slne.surf.cloud.api.common.player.teleport.WorldLocation
import dev.slne.surf.cloud.api.common.player.toOfflineCloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import kotlinx.serialization.Contextual
import net.kyori.adventure.text.ComponentLike
import java.time.ZonedDateTime
import java.util.*

data class UnstuckUsage(
    val uuid: @Contextual UUID,
    val executedAt: @Contextual ZonedDateTime,
    val server: String,
    val location: WorldLocation,
    var result: DbResult? = null,
    var acknowledgedBy: @Contextual UUID? = null,
) {
    val cloudPlayer get() = uuid.toOfflineCloudPlayer(false)

    companion object {
        val STREAM_CODEC = streamCodec<SurfByteBuf, UnstuckUsage>({ buf, usage ->
            buf.writeUuid(usage.uuid)
            buf.writeZonedDateTime(usage.executedAt)
            buf.writeUtf(usage.server)
            WorldLocation.STREAM_CODEC.encode(buf, usage.location)
            buf.writeNullable(usage.result) { buf, r -> buf.writeEnum(r) }
            buf.writeNullable(usage.acknowledgedBy) { buf, u -> buf.writeUuid(u) }
        }, { buf ->
            UnstuckUsage(
                uuid = buf.readUuid(),
                executedAt = buf.readZonedDateTime(),
                server = buf.readUtf(),
                location = WorldLocation.STREAM_CODEC.decode(buf),
                result = buf.readNullable { it.readEnum<DbResult>() },
                acknowledgedBy = buf.readNullable { it.readUuid() }
            )
        })
    }

    enum class DbResult {
        SUCCESS,
        FAILED_CAN_BUILD;

        fun toResult(): Result = when (this) {
            SUCCESS -> Result.Success
            FAILED_CAN_BUILD -> Result.FailedCanBuild
        }
    }

    sealed class Result(
        val message: SurfComponentBuilder.() -> Unit
    ) : ComponentLike {
        override fun asComponent() = buildText(message)

        object Success : Result({
            success("Du wurdest erfolgreich zum Spawn teleportiert.")
            appendProtocolizedInformation()
        })

        object FailedCanBuild : Result({
            error("Die teleportation zum Spawn ist fehlgeschlagen, da du nicht Stuck bist.")
            appendProtocolizedInformation()
        })
    }
}

fun SurfComponentBuilder.appendProtocolizedInformation() {
    appendNewline(2)
    info("Die Verwendung des Befehls wurde protokolliert.")
}
