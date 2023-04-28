package dev.slne.unstuck.bukkit.command;

import dev.slne.unstuck.bukkit.command.commands.UnstuckCommand;
import dev.slne.unstuck.bukkit.command.commands.UnstuckTpCommand;

public class BukkitCommandManager {

    /**
     * Register all commands
     */
    public void registerCommands() {
        new UnstuckCommand();
        new UnstuckTpCommand();
    }

}
