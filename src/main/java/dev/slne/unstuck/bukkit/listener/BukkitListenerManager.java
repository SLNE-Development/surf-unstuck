package dev.slne.unstuck.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev.slne.unstuck.bukkit.BukkitMain;
import dev.slne.unstuck.bukkit.listener.listeners.PortalListener;
import dev.slne.unstuck.bukkit.listener.listeners.TabCompleteListener;

public class BukkitListenerManager {

    /**
     * Registers all plugin {@link org.bukkit.event.Listener}s
     */
    public void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        JavaPlugin plugin = BukkitMain.getInstance();

        pluginManager.registerEvents(new PortalListener(), plugin);
        pluginManager.registerEvents(new TabCompleteListener(), plugin);
    }

    /**
     * Unregisters all {@link org.bukkit.event.Listener}s
     */
    public void unregisterListeners() {
        HandlerList.unregisterAll(BukkitMain.getInstance());
    }

}
