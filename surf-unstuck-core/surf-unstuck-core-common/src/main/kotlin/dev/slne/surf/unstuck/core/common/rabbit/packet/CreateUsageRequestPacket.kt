package dev.slne.surf.unstuck.core.common.rabbit.packet

import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import dev.slne.surf.unstuck.core.common.usage.UnstuckUsage
import kotlinx.serialization.Serializable

@Serializable
data class CreateUsageRequestPacket(
    val usage: UnstuckUsage
) : RabbitRequestPacket<PrimitiveResponse.BooleanResponsePacket>()