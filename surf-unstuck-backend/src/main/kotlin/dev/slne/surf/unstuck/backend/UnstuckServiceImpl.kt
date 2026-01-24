package dev.slne.surf.unstuck.backend

import com.google.auto.service.AutoService
import dev.slne.surf.unstuck.core.usage.UnstuckUsage
import dev.slne.surf.unstuck.core.service.UnstuckService

@AutoService(UnstuckService::class)
class UnstuckServiceImpl: UnstuckService {
    override suspend fun createUsage(usage: UnstuckUsage) = unstuckRepository.createUsage(usage)
}