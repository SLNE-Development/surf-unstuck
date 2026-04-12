package dev.slne.surf.unstuck.core.common.util

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class WorldLocation(
    val worldUuid: SerializableUUID,
    val x: Double,
    val y: Double,
    val z: Double
)