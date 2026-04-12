package dev.slne.surf.unstuck.core.client.service

import dev.slne.surf.unstuck.core.client.ClientUnstuckInstance
import dev.slne.surf.unstuck.core.common.rabbit.packet.CreateUsageRequestPacket
import dev.slne.surf.unstuck.core.common.usage.UnstuckUsage

object UnstuckService {
    suspend fun createUsage(usage: UnstuckUsage) = ClientUnstuckInstance.rabbitApi.sendRequest(
        CreateUsageRequestPacket(usage)
    ).value
}