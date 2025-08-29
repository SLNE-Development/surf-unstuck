package dev.slne.surf.unstuck.server

import dev.slne.surf.cloud.api.server.plugin.CoroutineTransactional
import dev.slne.surf.unstuck.core.common.UnstuckUsage
import dev.slne.surf.unstuck.server.db.UnstuckUsageEntity
import org.springframework.stereotype.Repository

@Repository
@CoroutineTransactional
class UnstuckRepository {

    fun createUsage(usage: UnstuckUsage) {
        UnstuckUsageEntity.new {
            this.playerUuid = usage.uuid
            this.result = usage.result ?: error("Result is null")
            this.server = usage.server
            this.location = usage.location
        }
    }

}