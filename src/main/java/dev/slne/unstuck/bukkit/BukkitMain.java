package dev.slne.unstuck.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import dev.slne.unstuck.bukkit.instance.BukkitApi;
import dev.slne.unstuck.bukkit.instance.BukkitInstance;

public class BukkitMain extends JavaPlugin {

    private static BukkitMain instance;
    private static BukkitInstance bukkitInstance;

    @Override
    public void onLoad() {
        instance = this;
        bukkitInstance = new BukkitInstance();
        BukkitApi.setInstance(bukkitInstance);

        bukkitInstance.onLoad();
    }

    @Override
    public void onEnable() {
        bukkitInstance.onEnable();
    }

    @Override
    public void onDisable() {
        bukkitInstance.onDisable();
    }

    /**
     * Returns the instance of the plugin
     * 
     * @return The instance of the plugin
     */
    public static BukkitMain getInstance() {
        return instance;
    }

    /**
     * Returns the core instance of the plugin
     * 
     * @return The core instance of the plugin
     */
    public static BukkitInstance getBukkitInstance() {
        return bukkitInstance;
    }

}
