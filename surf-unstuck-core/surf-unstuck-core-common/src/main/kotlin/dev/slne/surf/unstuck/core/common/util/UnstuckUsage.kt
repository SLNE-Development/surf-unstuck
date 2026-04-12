package dev.slne.surf.unstuck.core.common.usage

import dev.slne.surf.api.core.messages.adventure.appendNewline
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.api.core.serializer.java.datetime.datetime.zdt.SerializableZonedDateTime
import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.unstuck.core.common.util.WorldLocation
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.ComponentLike

@Serializable
data class UnstuckUsage(
    val uuid: SerializableUUID,
    val executedAt: SerializableZonedDateTime,
    val server: String,
    val location: WorldLocation,
    var result: DbResult? = null,
    var acknowledgedBy: SerializableUUID? = null,
) {
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
            error("Die Teleportation zum Spawn ist fehlgeschlagen, da du nicht Stuck bist.")
            appendProtocolizedInformation()
        })
    }
}

fun SurfComponentBuilder.appendProtocolizedInformation() {
    appendNewline(2)
    info("Die Verwendung des Befehls wurde protokolliert.")
}
