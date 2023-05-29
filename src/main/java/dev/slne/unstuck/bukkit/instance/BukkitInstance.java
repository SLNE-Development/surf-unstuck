package dev.slne.unstuck.bukkit.instance;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.slne.unstuck.bukkit.BukkitMain;
import dev.slne.unstuck.bukkit.command.BukkitCommandManager;
import dev.slne.unstuck.bukkit.listener.BukkitListenerManager;
import dev.slne.unstuck.bukkit.utils.LoggingUtils;

public class BukkitInstance {

    private BukkitCommandManager commandManager;
    private BukkitListenerManager listenerManager;

    /**
     * Called when the plugin is loaded
     */
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(BukkitMain.getInstance()));
        commandManager = new BukkitCommandManager();
        listenerManager = new BukkitListenerManager();
        LoggingUtils.setuplogfolder();
    }

    /**
     * Called when the plugin is enabled
     */
    public void onEnable() {
        CommandAPI.onEnable();
        commandManager.registerCommands();
        listenerManager.registerListeners();
    }

    /**
     * Called when the plugin is disabled
     */
    public void onDisable() {
        listenerManager.unregisterListeners();
        CommandAPI.onDisable();
    }

    /**
     * Returns the {@link BukkitCommandManager}
     * 
     * @return the {@link BukkitCommandManager}
     */
    public BukkitCommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Returns the {@link BukkitListenerManager}
     * 
     * @return the {@link BukkitListenerManager}
     */
    public BukkitListenerManager getListenerManager() {
        return listenerManager;
    }

}
