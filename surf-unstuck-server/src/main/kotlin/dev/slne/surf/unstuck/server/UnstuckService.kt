package dev.slne.surf.unstuck.server

import dev.slne.surf.unstuck.core.common.UnstuckUsage
import org.springframework.stereotype.Service

@Service
class UnstuckService(
    private val unstuckRepository: UnstuckRepository
) {

    suspend fun createUsage(usage: UnstuckUsage) = unstuckRepository.createUsage(usage)

}