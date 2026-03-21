package dev.slne.surf.unstuck.core.usage

import dev.slne.surf.surfapi.core.api.messages.adventure.appendNewline
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.unstuck.core.util.WorldLocation
import net.kyori.adventure.text.ComponentLike
import java.time.ZonedDateTime
import java.util.*

data class UnstuckUsage(
    val uuid: UUID,
    val executedAt: ZonedDateTime,
    val server: String,
    val location: WorldLocation,
    var result: DbResult? = null,
    var acknowledgedBy: UUID? = null,
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
