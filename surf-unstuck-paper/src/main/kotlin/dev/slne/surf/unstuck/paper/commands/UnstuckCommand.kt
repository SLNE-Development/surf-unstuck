package dev.slne.surf.unstuck.paper.commands

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.unstuck.paper.dialogs.createUnstuckDialog
import dev.slne.surf.unstuck.paper.permission.PermissionRegistry
import java.time.ZonedDateTime
import java.util.*
import kotlin.time.Duration.Companion.seconds

val usedUnstuckCache = Caffeine.newBuilder()
    .expireAfterWrite(10.seconds)
    .build<UUID, ZonedDateTime> { _ ->
        ZonedDateTime.now()
    }

fun unstuckCommand() = commandTree("unstuck") {
    withPermission(PermissionRegistry.BASE)

    playerExecutor { player, _ ->
        val cacheEntry = usedUnstuckCache.getIfPresent(player.uniqueId)

        if (cacheEntry != null) {
            player.sendText {
                appendErrorPrefix()
                error("Du darfst den Befehl nicht so häufig benutzen.")
            }

            return@playerExecutor
        }

        player.showDialog(createUnstuckDialog())
    }
}