package dev.slne.surf.unstuck.microservice.handler

import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import dev.slne.surf.unstuck.core.common.rabbit.packet.CreateUsageRequestPacket
import dev.slne.surf.unstuck.microservice.repository.UnstuckRepository
import kotlinx.coroutines.launch

object UnstuckHandler {
    @RabbitHandler
    fun handleCreateUsagePacket(packet: CreateUsageRequestPacket) = packet.launch {
        packet.respond(PrimitiveResponse.BooleanResponsePacket(UnstuckRepository.createUsage(packet.usage)))
    }
}