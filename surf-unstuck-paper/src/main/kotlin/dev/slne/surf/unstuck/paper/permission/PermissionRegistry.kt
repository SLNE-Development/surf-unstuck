package dev.slne.surf.unstuck.paper.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.unstuck"
    private const val COMMAND_PREFIX = "$PREFIX.command"

    val BASE = create("$COMMAND_PREFIX.unstuck")
    val ALERT = create("$PREFIX.alert")

}