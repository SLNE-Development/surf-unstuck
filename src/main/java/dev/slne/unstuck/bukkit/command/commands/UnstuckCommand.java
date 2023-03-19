package dev.slne.unstuck.bukkit.command.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.slne.unstuck.bukkit.BukkitMain;
import dev.slne.unstuck.bukkit.utils.LocationSerializer;
import dev.slne.unstuck.bukkit.utils.LoggingUtils;
import dev.slne.unstuck.bukkit.utils.MessageManager;
import dev.slne.unstuck.bukkit.utils.UnstuckHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class UnstuckCommand {

	private static List<UnstuckHelper> randomCodes;
	private static CommandPermission commandPermission = CommandPermission.fromString("unstuck.use");

	public UnstuckCommand() {
		if (randomCodes != null) {
			throw new IllegalStateException("Unstuck helper list can only be initialized once");
		}

		randomCodes = new ArrayList<>();

		new UnstuckCommandWithoutCode();
		new UnstuckCommandWithCode();
	}

	private class UnstuckCommandWithoutCode extends CommandAPICommand {

		public UnstuckCommandWithoutCode() {
			super("stuck");
			withPermission(commandPermission);

			executesPlayer((player, args) -> {
				UUID uuid = player.getUniqueId();
				String code = ThreadLocalRandom.current().nextInt(10000, 100000 + 1) + "";

				if (findHelper(uuid) != null) {
					player.sendMessage(
							Component.text("Du kannst den Befehl nicht oft eingeben!", MessageManager.ERROR));
					return;
				}

				player.sendMessage(getInfoTextComponent(code));

				BukkitTask task = new BukkitRunnable() {

					@Override
					public void run() {

						UnstuckHelper helper = findHelper(uuid);

						if (helper != null && player != null) {
							randomCodes.remove(helper);
							player.sendMessage(Component.text("Die Anfrage ist abgelaufen.",
									MessageManager.ERROR));
						}
					}
				}.runTaskLaterAsynchronously(BukkitMain.getInstance(), 15 * 20l);

				UnstuckHelper helper = new UnstuckHelper(uuid, code);
				helper.setTask(task);
				randomCodes.add(helper);
			});

			register();
		}
	}

	private class UnstuckCommandWithCode extends CommandAPICommand {

		public UnstuckCommandWithCode() {
			super("stuck");
			withPermission(commandPermission);
			withArguments(new IntegerArgument(""));

			executesPlayer((player, args) -> {
				UUID uuid = player.getUniqueId();
				UnstuckHelper helper = findHelper(uuid);

				if (helper == null || !helper.getCode().equalsIgnoreCase(args[0].toString())) {
					player.sendMessage(Component.text(
							"Beim Ausführen des Befehls ist ein Fehler aufgetreten. Der Befehl wird selbständig erneut ausgeführt.",
							MessageManager.ERROR));
					player.performCommand("stuck");
					return;
				}

				randomCodes.remove(helper);

				if (canBuildAtLocation(player)) {
					player.sendMessage(MessageManager.getPrefix().append(Component
							.text("Du darfst diesen Befehl hier nicht verwenden! ", MessageManager.ERROR)
							.append(Component.text("(Die Nutzung wurde protokolliert!)",
									MessageManager.SPACER))));

					logAndNotify(player, false);
					return;
				}

				logAndNotify(player, true);

				Location worldSpawn = Bukkit.getWorlds().get(0).getSpawnLocation();
				player.teleportAsync(worldSpawn);
				player.sendMessage(MessageManager.getPrefix().append(Component
						.text("Du wurdest erfolgreich zum Spawn teleportiert! ", MessageManager.SUCCESS)
						.append(Component.text("(Die Nutzung wurde protokolliert!)", MessageManager.SPACER))));
			});

			register();
		}
	}

	private static UnstuckHelper findHelper(UUID uuid) {
		return randomCodes.stream().filter(helper -> helper.getUuid().equals(uuid)).findFirst().orElse(null);
	}

	private static boolean canBuildAtLocation(Player player) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		if (query.testBuild(BukkitAdapter.adapt(player.getLocation()), localPlayer)) {
			return true;
		}

		return false;
	}

	private static Component getInfoTextComponent(String code) {
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
										"/stuck " + code))
								.hoverEvent(HoverEvent.showText(
										Component.text("Klicke hier um zurück zum Spawn zu gelangen!",
												MessageManager.SPACER)))
								.append(Component.text("!",
										MessageManager.INFO))));

		return infoText.build();
	}

	private static void logAndNotify(Player player, boolean successful) {

		LoggingUtils.logToFile(player.getName() + " (" + player.getUniqueId() + ") hat Unstuck bei "
				+ LocationSerializer.getReadableString(player.getLocation())
				+ " verwendet! (Erfolgreich: " + successful
				+ ")");

		Builder builder = Component.text();
		builder.append(MessageManager.getPrefix());
		builder.append(player.displayName());
		builder.append(Component.text(successful ? " hat" : " wollte", MessageManager.INFO));
		builder.append(Component.text(" Unstuck bei ", MessageManager.INFO));
		builder.append(Component.text(LocationSerializer
				.getReadableString(player.getLocation()), MessageManager.VARIABLE_VALUE)
				.clickEvent(ClickEvent.runCommand(
						"/unstucktp " + LocationSerializer
								.getSerializedLocation(player.getLocation())))
				.hoverEvent(HoverEvent.showText(
						Component.text("Klicke hier um dich dorthin zu teleportieren!",
								MessageManager.SPACER))));
		builder.append(Component.text(successful ? " verwendet!" : " verwenden!", MessageManager.INFO));

		Bukkit.broadcast(builder.build(), "unstuck.notify");
	}

}
