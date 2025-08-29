package dev.slne.surf.unstuck.server.db

import dev.slne.surf.cloud.api.common.player.teleport.WorldLocation
import dev.slne.surf.cloud.api.server.exposed.table.AuditableLongEntity
import dev.slne.surf.cloud.api.server.exposed.table.AuditableLongEntityClass
import dev.slne.surf.unstuck.core.common.UnstuckUsage
import org.jetbrains.exposed.dao.id.EntityID

class UnstuckUsageEntity(id: EntityID<Long>) : AuditableLongEntity(id, UnstuckUsagesTable) {
    companion object : AuditableLongEntityClass<UnstuckUsageEntity>(UnstuckUsagesTable)

    var playerUuid by UnstuckUsagesTable.playerUuid
    var server by UnstuckUsagesTable.server
    var result by UnstuckUsagesTable.result

    var world by UnstuckUsagesTable.world
    var x by UnstuckUsagesTable.x
    var y by UnstuckUsagesTable.y
    var z by UnstuckUsagesTable.z

    var acknowledgedBy by UnstuckUsagesTable.acknowledgedBy

    var location: WorldLocation
        get() = WorldLocation(world, x, y, z)
        set(value) {
            world = value.world
            x = value.x
            y = value.y
            z = value.z
        }

    fun toDto() = UnstuckUsage(
        uuid = playerUuid,
        executedAt = createdAt,
        result = result,
        server = server,
        acknowledgedBy = acknowledgedBy,
        location = location
    )
}