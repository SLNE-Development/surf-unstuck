package dev.slne.surf.unstuck.microservice.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.table.AuditableLongIdTable
import dev.slne.surf.unstuck.core.common.usage.UnstuckUsage

object UnstuckUsagesTable : AuditableLongIdTable("unstuck_usages") {
    val playerUuid = nativeUuid("player_uuid")
    val server = varchar("server", 255)
    val result = enumerationByName<UnstuckUsage.DbResult>("result", 50)

    val world = nativeUuid("world")
    val x = double("x")
    val y = double("y")
    val z = double("z")

    val acknowledgedBy = nativeUuid("acknowledged_by").nullable()
}