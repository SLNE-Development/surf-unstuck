package dev.slne.surf.unstuck.core.common.netty.protocol.serverbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import dev.slne.surf.unstuck.core.common.UnstuckUsage

@SurfNettyPacket(id = "unstuck:log_unstuck_usage", flow = PacketFlow.SERVERBOUND)
class ServerboundCreateUnstuckUsagePacket(
    val unstuckUsage: UnstuckUsage
) : NettyPacket()