package dev.slne.unstuck.unstuck.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class TabCompleteListener implements Listener {

    @EventHandler
    public void onTabComplete(PlayerCommandSendEvent event) {
        if (event.getPlayer().hasPermission("unstuck.use")) {
            event.getCommands().add("unstuck");
        }
    }

}
