package dev.slne.surf.unstuck.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.unstuck.backend.repository.unstuckRepository
import dev.slne.surf.unstuck.core.service.UnstuckService
import dev.slne.surf.unstuck.core.usage.UnstuckUsage

@AutoService(UnstuckService::class)
class UnstuckServiceImpl: UnstuckService {
    override suspend fun createUsage(usage: UnstuckUsage) = unstuckRepository.createUsage(usage)
}