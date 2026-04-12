package dev.slne.surf.unstuck.core.common

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.rabbitmq.api.RabbitMQApi

private val instance = requiredService<UnstuckInstance>()

interface UnstuckInstance {
    val rabbitApi: RabbitMQApi

    companion object : UnstuckInstance by instance {
        val INSTANCE get() = instance
    }
}