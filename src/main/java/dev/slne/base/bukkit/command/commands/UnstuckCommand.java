package dev.slne.base.bukkit.command.commands;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import dev.slne.base.bukkit.BukkitMain;
import dev.slne.base.bukkit.utils.LocationSerializer;
import dev.slne.base.bukkit.utils.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class UnstuckCommand implements CommandExecutor, TabCompleter {

        private HashMap<UUID, String> randomCodes = new HashMap<>();

        public UnstuckCommand(PluginCommand command) {
                command.setExecutor(this);
                command.setTabCompleter(this);
        }

        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

                if (!(sender instanceof Player)) {
                        return true;
                }

                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();

                if (args.length == 0 || !args[0].equals(randomCodes.get(uuid))) {
                        String code = ThreadLocalRandom.current().nextInt(10000, 100000 + 1) + "";
                        randomCodes.put(uuid, code);
                        player.sendMessage(getInfoTextComponent(code));

                        new BukkitRunnable() {

                                @Override
                                public void run() {
                                        if (randomCodes.get(uuid) != null) {
                                                randomCodes.remove(uuid);
                                                player.sendMessage(Component.text("Die Anfrage ist abgelaufen.",
                                                                MessageManager.ERROR));
                                        }
                                }

                        }.runTaskLaterAsynchronously(BukkitMain.getInstance(), 15 * 20l);

                        return true;

                }

                randomCodes.remove(uuid);

                if (canBuildAtLocation(player)) {
                        player.sendMessage(MessageManager.getPrefix().append(Component
                                        .text("Du darfst diesen Befehl hier nicht verwenden! ", MessageManager.ERROR)
                                        .append(Component.text("(Die Nutzung wurde protokolliert!)",
                                                        MessageManager.SPACER))));

                        logAndNotify(player, false);
                        return true;
                }

                logAndNotify(player, true);

                Location worldSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();
                player.teleportAsync(worldSpawn);
                player.sendMessage(MessageManager.getPrefix().append(Component
                                .text("Du wurdest erfolgreich zum Spawn teleportiert! ", MessageManager.SUCCESS)
                                .append(Component.text("(Die Nutzung wurde protokolliert!)", MessageManager.SPACER))));

                return true;
        }

        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
                return null;
        }

        public boolean canBuildAtLocation(Player player) {
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();
                LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

                if (query.testBuild(BukkitAdapter.adapt(player.getLocation()), localPlayer)) {
                        return true;
                }

                return false;
        }

        public Component getInfoTextComponent(String code) {
                Builder infoText = Component.text();

                infoText.append(
                                Component.text("Du steckst auf einem Grundstück fest?",
                                                MessageManager.INFO));
                infoText.append(Component.newline());
                infoText.append(
                                Component.text("Bitte beachte folgende Informationen:",
                                                MessageManager.INFO));
                infoText.append(Component.newline());
                infoText.append(Component.newline());
                infoText.append(Component.text(
                                "Der Befehl darf nur verwendet werden, wenn du auch tatsächlich feststeckst.",
                                MessageManager.IMPORTANT));
                infoText.append(Component.newline());
                infoText.append(Component.text(
                                "Der Missbrauch des Befehls um zurück zum Spawn zu gelangen wird als Exploiting angesehen und bestraft!",
                                MessageManager.IMPORTANT));
                infoText.append(Component.newline());
                infoText.append(Component.newline());
                infoText.append(Component.text("Möchtest du den Befehl wirklich verwenden?",
                                MessageManager.INFO));
                infoText.append(Component.newline());
                infoText.append(Component.text("Falls ja, ",
                                MessageManager.INFO).append(
                                                Component.text("*klicke hier*", MessageManager.VARIABLE_VALUE)
                                                                .clickEvent(ClickEvent.runCommand(
                                                                                "/surfunstuck:unstuck " + code))
                                                                .hoverEvent(HoverEvent.showText(
                                                                                Component.text("Klicke hier um zurück zum Spawn zu gelangen!",
                                                                                                MessageManager.SPACER)))
                                                                .append(Component.text("!",
                                                                                MessageManager.INFO))));

                return infoText.build();
        }

        public void logAndNotify(Player player, boolean successful) {

                Logger log = Bukkit.getLogger();
                log.info("[Unstuck] " + player.getName() + " (" + player.getUniqueId() + ") hat Unstuck bei "
                                + LocationSerializer.getReadableString(player.getLocation())
                                + " verwendet! (Erfolgreich: " + successful
                                + " )");

                Builder builder = Component.text();
                builder.append(MessageManager.getPrefix());
                builder.append(player.displayName());
                builder.append(Component.text(successful ? " hat" : " wollte", MessageManager.INFO));
                builder.append(Component.text(" Unstuck bei ", MessageManager.INFO));
                builder.append(Component.text(LocationSerializer
                                .getReadableString(player.getLocation()), MessageManager.VARIABLE_VALUE)
                                .clickEvent(ClickEvent.runCommand(
                                                "/surfunstuck:unstucktp " + LocationSerializer
                                                                .getSerializedLocation(player.getLocation())))
                                .hoverEvent(HoverEvent.showText(
                                                Component.text("Klicke hier um dich dorthin zu teleportieren!",
                                                                MessageManager.SPACER))));
                builder.append(Component.text(successful ? " verwendet!" : " verwenden!", MessageManager.INFO));

                Bukkit.broadcast(builder.build(), "unstuck.notify");
        }
}
