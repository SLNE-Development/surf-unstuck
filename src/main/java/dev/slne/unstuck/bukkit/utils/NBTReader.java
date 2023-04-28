package dev.slne.unstuck.bukkit.utils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;

public class NBTReader {

    public static boolean setLocation(OfflinePlayer player, Location location) throws IOException {
        UUID uuid = player.getUniqueId();
        File dataFile = getPlayerFile(uuid);

        if (dataFile == null) {
            return false;
        }

        CompoundBinaryTag rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), BinaryTagIO.Compression.GZIP);
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder().put(rawTag);

        System.out.println(location.getWorld().getKey().asString());

        World world = location.getWorld();
        builder.put("Dimension", StringBinaryTag.of(world.getKey().asString()));

        long least = world.getUID().getLeastSignificantBits();
        long most = world.getUID().getMostSignificantBits();
        builder.put("WorldUUIDLeast", LongBinaryTag.of(least));
        builder.put("WorldUUIDMost", LongBinaryTag.of(most));

        ListBinaryTag.Builder<BinaryTag> posTag = ListBinaryTag.builder();
        posTag.add(DoubleBinaryTag.of(location.getX()));
        posTag.add(DoubleBinaryTag.of(location.getY()));
        posTag.add(DoubleBinaryTag.of(location.getZ()));

        ListBinaryTag.Builder<BinaryTag> rotTag = ListBinaryTag.builder();
        rotTag.add(FloatBinaryTag.of(location.getYaw()));
        rotTag.add(FloatBinaryTag.of(location.getPitch()));

        builder.put("Pos", posTag.build());
        builder.put("Rotation", rotTag.build());

        BinaryTagIO.writer().write(builder.build(), dataFile.toPath(), BinaryTagIO.Compression.GZIP);
        return true;
    }

    private static File getPlayerFile(UUID uuid) {
        for (World world : Bukkit.getWorlds()) {
            File worldFolder = world.getWorldFolder();
            if (!worldFolder.isDirectory()) {
                continue;
            }

            File[] children = worldFolder.listFiles();

            if (children == null) {
                continue;
            }

            for (File file : children) {
                if (!file.isDirectory() || !file.getName().equals("playerdata")) {
                    continue;
                }

                return getPlayerFile(file, uuid);
            }
        }

        return null;
    }

    private static File getPlayerFile(File playerDataFolder, UUID uuid) {
        File[] files = playerDataFolder.listFiles();

        if (files == null) {
            return null;
        }

        for (File file : files) {
            if (file.getName().equals(uuid.toString() + ".dat")) {
                return file;
            }
        }

        return null;
    }
}
