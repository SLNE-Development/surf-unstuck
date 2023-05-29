package dev.slne.unstuck.bukkit.instance;

public class BukkitApi {

    private static BukkitInstance instance;

    /**
     * Sets the instance of the plugin
     * 
     * @param instance The instance of the plugin
     */
    public static void setInstance(BukkitInstance instance) {
        BukkitApi.instance = instance;
    }

    /**
     * Returns the instance of the plugin
     * 
     * @return The instance of the plugin
     */
    public static BukkitInstance getInstance() {
        return instance;
    }

}
