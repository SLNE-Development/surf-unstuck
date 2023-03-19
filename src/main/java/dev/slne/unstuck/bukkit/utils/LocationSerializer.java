package dev.slne.unstuck.bukkit.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer {

    public static String getSerializedLocation(Location location) {
        return location.getWorld().getUID() + "_" + location.getX() + "_" + location.getY() + "_" + location.getZ()
                + "_" + location.getYaw() + "_" + location.getPitch();
    }

    public static String getReadableString(Location location) {
        return "[" + location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " "
                + location.getBlockZ() + "]";
    }

    public static Location getDeserializedLocation(String string) {
        String[] parts = string.split("_");
        UUID uuid = UUID.fromString(parts[0]);
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);
        World world = Bukkit.getServer().getWorld(uuid);
        return new Location(world, x, y, z, yaw, pitch);
    }

}
