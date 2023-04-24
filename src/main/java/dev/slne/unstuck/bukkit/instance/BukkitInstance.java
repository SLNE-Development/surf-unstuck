package dev.slne.unstuck.bukkit.instance;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.slne.data.bukkit.BukkitDataSource;
import dev.slne.unstuck.bukkit.BukkitMain;
import dev.slne.unstuck.bukkit.command.BukkitCommandManager;
import dev.slne.unstuck.bukkit.listener.BukkitListenerManager;
import dev.slne.unstuck.bukkit.utils.LoggingUtils;
import dev.slne.unstuck.core.instance.CoreInstance;

public class BukkitInstance extends CoreInstance {

    private BukkitCommandManager commandManager;
    private BukkitListenerManager listenerManager;

    private BukkitDataSource dataSource;

    @Override
    public void onLoad() {
        super.onLoad();

        CommandAPI.onLoad(new CommandAPIBukkitConfig(BukkitMain.getInstance()));
        commandManager = new BukkitCommandManager();
        listenerManager = new BukkitListenerManager();
        LoggingUtils.setuplogfolder();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        CommandAPI.onEnable();
        commandManager.registerCommands();
        listenerManager.registerListeners();
    }

    @Override
    public void onDisable() {
        super.onDisable();

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

    /**
     * Returns the {@link BukkitDataSource}
     * 
     * @return the {@link BukkitDataSource}
     */
    public BukkitDataSource getDataSource() {
        return dataSource;
    }

}
