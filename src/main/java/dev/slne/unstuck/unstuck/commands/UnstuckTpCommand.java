package dev.slne.unstuck.unstuck.commands;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import dev.slne.unstuck.unstuck.utils.LocationSerializer;
import dev.slne.unstuck.unstuck.utils.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;

public class UnstuckTpCommand implements CommandExecutor, TabCompleter {

    public UnstuckTpCommand(PluginCommand command) {
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        if (args.length != 1) {
            return true;
        }

        Location location = LocationSerializer.getDeserializedLocation(args[0]);

        if (location == null) {
            return true;
        }

        Player player = (Player) sender;

        Builder builder = Component.text();
        builder.append(MessageManager.getPrefix());
        builder.append(Component.text("Du wurdest zu ", MessageManager.SUCCESS));
        builder.append(
                Component.text(location.getBlockX() + ", " + location.getBlockY() + ", " + location.blockZ() + " ("
                        + location.getWorld().getName() + ") ", MessageManager.VARIABLE_VALUE));
        builder.append(Component.text("teleportiert!", MessageManager.SUCCESS));

        player.teleportAsync(location);
        player.sendMessage(builder.build());

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

}
