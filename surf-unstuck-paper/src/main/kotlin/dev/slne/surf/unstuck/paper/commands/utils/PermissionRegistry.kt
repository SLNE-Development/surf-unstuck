package dev.slne.surf.unstuck.paper.commands.utils

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {

    private const val PREFIX = "surf.unstuck"
    private const val COMMAND_PREFIX = "$PREFIX.command"

    val BASE = "$COMMAND_PREFIX.unstuck"

}