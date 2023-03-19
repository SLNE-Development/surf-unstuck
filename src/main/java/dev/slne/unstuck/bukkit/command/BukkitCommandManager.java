package dev.slne.unstuck.bukkit.command;

import org.bukkit.plugin.java.JavaPlugin;

import dev.slne.unstuck.bukkit.BukkitMain;
import dev.slne.unstuck.bukkit.command.commands.UnstuckCommand;
import dev.slne.unstuck.bukkit.command.commands.UnstuckTpCommand;

public class BukkitCommandManager {

    /**
     * Register all commands
     */
    public void registerCommands() {
        JavaPlugin plugin = BukkitMain.getInstance();
        new UnstuckCommand(plugin.getCommand("stuck"));
        new UnstuckTpCommand(plugin.getCommand("unstucktp"));
    }

}
