package me.h1dd3nxn1nja.chatmanager.paper.commands;

import me.h1dd3nxn1nja.chatmanager.paper.ChatManager;
import me.h1dd3nxn1nja.chatmanager.paper.Methods;
import me.h1dd3nxn1nja.chatmanager.paper.managers.PlaceholderManager;
import me.h1dd3nxn1nja.chatmanager.paper.support.EssentialsSupport;
import me.h1dd3nxn1nja.chatmanager.paper.support.PluginSupport;
import com.ryderbelserion.chatmanager.paper.files.Files;
import me.h1dd3nxn1nja.chatmanager.paper.support.vanish.EssentialsVanishSupport;
import me.h1dd3nxn1nja.chatmanager.paper.support.vanish.GenericVanishSupport;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class CommandMessage implements CommandExecutor {

	private final ChatManager plugin = ChatManager.getPlugin();
	private final GenericVanishSupport genericVanishSupport = plugin.getPluginManager().getGenericVanishSupport();
	private final PlaceholderManager placeholderManager = plugin.getCrazyManager().getPlaceholderManager();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		FileConfiguration messages = Files.MESSAGES.getFile();

		String playerNotFound = messages.getString("Message.Player_Not_Found");

		if (sender instanceof ConsoleCommandSender) {
			plugin.getLogger().warning("This command can only be used by a player.");
			return true;
		}

		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("Message")) {
			if (player.hasPermission("chatmanager.message")) {
				StringBuilder message = new StringBuilder();

				for (int i = 1; i < args.length; i++) {
					message.append(args[i]).append(" ");
				}

				if (args.length < 1) {
					this.plugin.getMethods().sendMessage(player, "&cCommand Usage: &7/Message <player> <message>", true);
					return true;
				}

				Player target = plugin.getServer().getPlayer(args[0]);

				if (target == null || !target.isOnline()) {
					if (playerNotFound != null) this.plugin.getMethods().sendMessage(player, playerNotFound.replace("{target}", args[0]), true);
					return true;
				}

				if ((target == player) && (!player.hasPermission("chatmanager.message.self"))) {
					this.plugin.getMethods().sendMessage(player, messages.getString("Private_Message.Self"), true);
					return true;
				}

				if ((target.getGameMode().equals(GameMode.SPECTATOR) && (!player.hasPermission("chatmanager.bypass.spectator")))) {
					if (playerNotFound != null) this.plugin.getMethods().sendMessage(player, playerNotFound.replace("{target}", args[0]), true);
					return true;
				}

				if (plugin.api().getToggleMessageData().containsUser(target.getUniqueId()) && !player.hasPermission("chatmanager.bypass.togglepm")) {
					this.plugin.getMethods().sendMessage(player, messages.getString("Private_Message.Toggled"), true);
					return true;
				}

				if ((!player.canSee(target)) && (!player.hasPermission("chatmanager.bypass.vanish"))) {
					if (playerNotFound != null) this.plugin.getMethods().sendMessage(player, playerNotFound.replace("{target}", args[0]), true);
					return true;
				}

				if (duplicate(args, playerNotFound, player, message, target, player.getUniqueId(), target.getUniqueId())) return true;
			} else {
				player.sendMessage(this.plugin.getMethods().noPermission());
			}
		}

		if (cmd.getName().equalsIgnoreCase("Reply")) {
			if (player.hasPermission("chatmanager.reply")) {
				if (args.length > 0) {
					StringBuilder message = new StringBuilder();

					for (String arg : args) {
						message.append(arg).append(" ");
					}

					UUID other = plugin.api().getUserRepliedData().getUser(player.getUniqueId());

					Player target = plugin.getServer().getPlayer(other);

					if (target == null || !target.isOnline()) {
						this.plugin.getMethods().sendMessage(player, messages.getString("Private_Message.Recipient_Not_Found"), true);
						return true;
					}

					if (plugin.api().getToggleMessageData().containsUser(target.getUniqueId())) {
						this.plugin.getMethods().sendMessage(player, messages.getString("Private_Message.Toggled"), true);
						return true;
					}

					if (!player.canSee(target)) {
						if (playerNotFound != null) this.plugin.getMethods().sendMessage(player, playerNotFound.replace("{target}", args[0]), true);
						return true;
					}

					if (duplicate(args, playerNotFound, player, message, target, target.getUniqueId(), player.getUniqueId())) return true;
				} else {
					player.sendMessage(this.plugin.getMethods().color("&cCommand Usage: &7/Reply <message>"));
				}
			} else {
				player.sendMessage(this.plugin.getMethods().noPermission());
			}
		}

		if (cmd.getName().equalsIgnoreCase("TogglePM")) {
			if (player.hasPermission("chatmanager.toggle.pm")) {
				if (args.length == 0) {

					boolean isValid = plugin.api().getToggleMessageData().containsUser(player.getUniqueId());

					if (isValid) {
						plugin.api().getToggleMessageData().removeUser(player.getUniqueId());
						this.plugin.getMethods().sendMessage(player, messages.getString("TogglePM.Disabled"), true);
						return true;
					}

					plugin.api().getToggleMessageData().addUser(player.getUniqueId());
					this.plugin.getMethods().sendMessage(player, messages.getString("TogglePM.Enabled"), true);

					return true;
				} else {
					this.plugin.getMethods().sendMessage(player, "&cCommand Usage: &7/Togglepm", true);
				}
			} else {
				this.plugin.getMethods().sendMessage(player, this.plugin.getMethods().noPermission(), true);
			}
		}

		return true;
	}

	private boolean duplicate(String[] args, String playerNotFound, Player player, StringBuilder message, Player target, UUID uniqueId, UUID uniqueId2) {
		FileConfiguration config = Files.CONFIG.getFile();
		FileConfiguration messages = Files.MESSAGES.getFile();

		if (message.isEmpty()) {
			player.sendMessage(plugin.getMethods().color(plugin.getMethods().getPrefix() + "You need to supply a message in order to reply/send to " + target.getName()));
			return true;
		}

		if (essentialsCheck(args, playerNotFound, player, target)) return true;

		if (PluginSupport.PREMIUM_VANISH.isPluginEnabled() || PluginSupport.SUPER_VANISH.isPluginEnabled() && genericVanishSupport.isVanished(target) && !player.hasPermission("chatmanager.bypass.vanish")) {
			if (playerNotFound != null) this.plugin.getMethods().sendMessage(player, playerNotFound.replace("{target}", args[0]), true);
			return true;
		}

		this.plugin.getMethods().sendMessage(player, placeholderManager.setPlaceholders(target, config.getString("Private_Messages.Sender.Format")
				.replace("{receiver}", target.getName())
				.replace("{receiver_displayname}", target.getDisplayName()) + message), true);

		this.plugin.getMethods().sendMessage(target, placeholderManager.setPlaceholders(player, config.getString("Private_Messages.Receiver.Format")
				.replace("{receiver}", target.getName())
				.replace("{receiver_displayname}", player.getDisplayName()) + message), true);

		this.plugin.getMethods().playSound(target, config, "Private_Messages.sound");

		plugin.api().getUserRepliedData().addUser(uniqueId, uniqueId2);
		plugin.api().getUserRepliedData().addUser(uniqueId2, uniqueId);

		for (Player staff : plugin.getServer().getOnlinePlayers()) {
			if ((staff != player) && (staff != target)) {
				if ((!player.hasPermission("chatmanager.bypass.socialspy")) && (!target.hasPermission("chatmanager.bypass.socialspy"))) {
					boolean contains = plugin.api().getSocialSpyData().containsUser(staff.getUniqueId());

					if (contains) this.plugin.getMethods().sendMessage(staff, messages.getString("Social_Spy.Format").replace("{player}", player.getName()).replace("{receiver}", target.getName()).replace("{message}", message), true);
				}
			}
		}
		return false;
	}

	private final EssentialsSupport essentialsSupport = plugin.getPluginManager().getEssentialsSupport();
	private final EssentialsVanishSupport essentialsVanishSupport = plugin.getPluginManager().getEssentialsVanishSupport();

	private boolean essentialsCheck(String[] args, String playerNotFound, Player player, Player target) {
		FileConfiguration messages = Files.MESSAGES.getFile();

		if (PluginSupport.ESSENTIALS.isPluginEnabled()) {
			if (essentialsSupport.getUser(target).isAfk() && (!player.hasPermission("chatmanager.bypass.afk"))) {
				this.plugin.getMethods().sendMessage(player, messages.getString("Private_Message.AFK").replace("{target}", target.getName()), true);
				return true;
			}

			if (essentialsSupport.isIgnored(target, player) && (!player.hasPermission("chatmanager.bypass.ignored"))) {
				this.plugin.getMethods().sendMessage(player, messages.getString("Private_Message.Ignored").replace("{target}", target.getName()), true);
				return true;
			}

			if (essentialsVanishSupport.isVanished(target) && (!player.hasPermission("chatmanager.bypass.vanish"))) {
				if (playerNotFound != null) this.plugin.getMethods().sendMessage(player, playerNotFound.replace("{target}", args[0]), true);
				return true;
			}

			return essentialsSupport.isMuted(player);
		}

		return false;
	}
}