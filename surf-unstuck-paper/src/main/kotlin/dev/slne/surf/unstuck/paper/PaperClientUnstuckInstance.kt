package dev.slne.surf.unstuck.paper

import com.google.auto.service.AutoService
import dev.slne.surf.unstuck.core.client.ClientLoader
import dev.slne.surf.unstuck.core.client.ClientUnstuckInstance
import dev.slne.surf.unstuck.core.common.UnstuckInstance
import net.kyori.adventure.util.Services

@AutoService(UnstuckInstance::class)
class PaperClientUnstuckInstance : ClientUnstuckInstance, Services.Fallback {
    override val clientLoader = ClientLoader(plugin.dataPath)
}