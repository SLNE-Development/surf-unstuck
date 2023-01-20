package dev.slne.unstuck.unstuck.listeners;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import dev.slne.unstuck.bukkit.BukkitMain;
import dev.slne.unstuck.unstuck.utils.MessageManager;
import dev.slne.unstuck.unstuck.utils.NBTReader;
import net.kyori.adventure.text.Component;

public class PortalListener implements Listener {

    private HashMap<UUID, Location> locations = new HashMap<>();
    private HashMap<UUID, BukkitTask> tasks = new HashMap<>();

    @EventHandler
    public void onPortalExit(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Location oldLoc = event.getFrom();
        Instant timeTeleported = Instant.now();

        if (!(event.getCause().equals(TeleportCause.NETHER_PORTAL))) {
            return;
        }

        locations.put(uuid, oldLoc);

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {

                if (!isInPortal(player)) {
                    this.cancel();
                }

                if (timeTeleported.toEpochMilli() <= Instant.now().toEpochMilli() - 10 * 1000) {
                    player.teleport(oldLoc);
                    player.sendMessage(Component
                            .text("Du hast das Portal nicht rechtzeitig verlassen und wurdest zurück teleportiert.",
                                    MessageManager.ERROR));
                    locations.remove(uuid);
                    tasks.remove(uuid);
                    this.cancel();
                }

            }

        }.runTaskTimer(BukkitMain.getInstance(), 20l, 20l);

        tasks.put(uuid, task);
    }

    @EventHandler
    public void setLocationOnDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            public void run() {
                setLocation(player);
            };
        }.runTaskLater(BukkitMain.getInstance(), 1);
    }

    private boolean setLocation(Player player) {
        UUID uuid = player.getUniqueId();

        if (locations == null || !locations.containsKey(uuid)) {
            return false;
        }

        if (!isInPortal(player)) {
            locations.remove(uuid);
            return false;
        }

        Location location = locations.get(uuid);
        locations.remove(uuid);

        try {
            boolean success = NBTReader.setLocation(player, location);
            return success;
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return false;
    }

    @EventHandler
    public void deleteTaskOnDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!tasks.containsKey(uuid)) {
            return;
        }

        BukkitTask task = tasks.get(uuid);

        if (task == null) {
            return;
        }

        try {
            if (!task.isCancelled()) {
                task.cancel();
                tasks.remove(uuid);
            }
        } catch (IllegalStateException exception) {
            // IGNORE
        }
    }

    private boolean isInPortal(Player player) {
        Location playerLoc = player.getLocation();
        Block block = playerLoc.getBlock();
        ArrayList<Material> blocks = new ArrayList<Material>();

        blocks.add(block.getType());
        blocks.add(block.getRelative(BlockFace.UP, 1).getType());

        if (blocks.get(0).equals(Material.AIR) && blocks.get(1).equals(Material.AIR)) {
            return false;
        }

        blocks.add(block.getRelative(BlockFace.NORTH, 1).getType());
        blocks.add(block.getRelative(BlockFace.EAST, 1).getType());
        blocks.add(block.getRelative(BlockFace.SOUTH, 1).getType());
        blocks.add(block.getRelative(BlockFace.WEST, 1).getType());

        if (!blocks.contains(Material.NETHER_PORTAL)) {
            return false;
        }

        return true;
    }

}
