package dev.slne.surf.unstuck.microservice.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.unstuck.core.common.usage.UnstuckUsage
import dev.slne.surf.unstuck.microservice.table.UnstuckUsagesTable
import java.time.OffsetDateTime

object UnstuckRepository {
    suspend fun createUsage(usage: UnstuckUsage) = suspendTransaction {
        UnstuckUsagesTable.insert {
            it[playerUuid] = usage.uuid
            it[result] = usage.result ?: error("Result is null")
            it[server] = usage.server
            it[world] = usage.location.worldUuid
            it[x] = usage.location.x
            it[y] = usage.location.y
            it[z] = usage.location.z
            it[updatedAt] = OffsetDateTime.now()
            it[createdAt] = OffsetDateTime.now()
        }

        true
    }
}