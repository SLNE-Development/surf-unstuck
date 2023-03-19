package dev.slne.unstuck.bukkit.command.commands;

import org.bukkit.Location;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.unstuck.bukkit.utils.LocationSerializer;
import dev.slne.unstuck.bukkit.utils.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;

public class UnstuckTpCommand extends CommandAPICommand {

    public UnstuckTpCommand() {
        super("unstucktp");
        withPermission("unstuck.tp");
        withArguments(new StringArgument("serializedLocation"));

        executesPlayer((player, args) -> {
            Location location = LocationSerializer.getDeserializedLocation(args[0].toString());

            if (location == null) {
                return;
            }

            Builder builder = Component.text();
            builder.append(MessageManager.getPrefix());
            builder.append(Component.text("Du wurdest zu ", MessageManager.SUCCESS));
            builder.append(
                    Component.text(location.getBlockX() + ", " + location.getBlockY() + ", " + location.blockZ() + " ("
                            + location.getWorld().getName() + ") ", MessageManager.VARIABLE_VALUE));
            builder.append(Component.text("teleportiert!", MessageManager.SUCCESS));

            player.teleportAsync(location);
            player.sendMessage(builder.build());
        });

        register();
    }
}
