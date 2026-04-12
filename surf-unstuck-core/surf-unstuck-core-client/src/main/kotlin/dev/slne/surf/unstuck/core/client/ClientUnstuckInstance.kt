package dev.slne.surf.unstuck.core.client

import dev.slne.surf.rabbitmq.api.ClientRabbitMQApi
import dev.slne.surf.unstuck.core.common.UnstuckInstance

interface ClientUnstuckInstance : UnstuckInstance {
    val clientLoader: ClientLoader

    override val rabbitApi: ClientRabbitMQApi get() = clientLoader.rabbitApi

    companion object : ClientUnstuckInstance by UnstuckInstance.INSTANCE as ClientUnstuckInstance {
        val INSTANCE get() = UnstuckInstance.INSTANCE
    }
}