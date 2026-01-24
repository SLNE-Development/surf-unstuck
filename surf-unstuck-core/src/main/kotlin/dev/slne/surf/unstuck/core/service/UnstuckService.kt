package dev.slne.surf.unstuck.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.unstuck.core.usage.UnstuckUsage

val unstuckService = requiredService<UnstuckService>()

interface UnstuckService {
    suspend fun createUsage(usage: UnstuckUsage)
}