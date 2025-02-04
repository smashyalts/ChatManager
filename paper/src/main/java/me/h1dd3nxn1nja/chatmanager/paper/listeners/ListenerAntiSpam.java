package me.h1dd3nxn1nja.chatmanager.paper.listeners;

import java.util.List;
import java.util.UUID;
import com.ryderbelserion.chatmanager.paper.files.Files;
import me.h1dd3nxn1nja.chatmanager.paper.ChatManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ListenerAntiSpam implements Listener {

	private final ChatManager plugin = ChatManager.getPlugin();

	@EventHandler(ignoreCancelled = true)
	public void antiSpamChat(AsyncPlayerChatEvent event) {
		FileConfiguration config = Files.CONFIG.getFile();
		FileConfiguration messages = Files.MESSAGES.getFile();

		Player player = event.getPlayer();
		String message = event.getMessage();

		UUID uuid = player.getUniqueId();

		boolean isValid = plugin.api().getStaffChatData().containsUser(uuid);

		if (isValid) return;

		if (config.getBoolean("Anti_Spam.Chat.Block_Repetitive_Messages")) {
			if (player.hasPermission("chatmanager.bypass.dupe.chat")) return;

			if (plugin.api().getPreviousMsgData().containsUser(uuid)) {

				String msg = plugin.api().getPreviousMsgData().getMessage(player.getUniqueId());

				if (message.equalsIgnoreCase(msg)) {
					player.sendMessage(this.plugin.getMethods().color(player.getUniqueId(), messages.getString("Anti_Spam.Chat.Repetitive_Message").replace("{Prefix}", messages.getString("Message.Prefix"))));
					event.setCancelled(true);
				}
			}

			plugin.api().getPreviousMsgData().addUser(uuid, message);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onChatCoolDown(AsyncPlayerChatEvent event) {
		FileConfiguration config = Files.CONFIG.getFile();
		FileConfiguration messages = Files.MESSAGES.getFile();

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		boolean isValid = plugin.api().getStaffChatData().containsUser(uuid);

		if (isValid) return;

		int delay = config.getInt("Anti_Spam.Chat.Chat_Delay");

		if (delay == 0 || player.hasPermission("chatmanager.bypass.chatdelay")) return;

		if (plugin.api().getChatCooldowns().containsUser(uuid)) {
			int time = plugin.api().getChatCooldowns().getTime(uuid);

			this.plugin.getMethods().sendMessage(player, messages.getString("Anti_Spam.Chat.Delay_Message").replace("{Time}", String.valueOf(time)), true);
			event.setCancelled(true);
			return;
		}

		plugin.api().getChatCooldowns().addUser(uuid, config.getInt("Anti_Spam.Chat.Chat_Delay"));

		plugin.api().getCooldownTask().addUser(uuid, new BukkitRunnable() {
			@Override
			public void run() {
				int time = plugin.api().getChatCooldowns().getTime(uuid);

				plugin.api().getChatCooldowns().subtract(uuid);

				if (time == 0) {
					plugin.api().getChatCooldowns().removeUser(uuid);
					plugin.api().getCooldownTask().removeUser(uuid);
					cancel();
				}
			}
		});

		plugin.api().getCooldownTask().getUsers().get(player.getUniqueId()).runTaskTimer(plugin, 20L, 20L);
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpamCommand(PlayerCommandPreprocessEvent event) {
		FileConfiguration config = Files.CONFIG.getFile();
		FileConfiguration messages = Files.MESSAGES.getFile();

		Player player = event.getPlayer();
		String command = event.getMessage();

		UUID uuid = player.getUniqueId();

		List<String> whitelistedCommands = config.getStringList("Anti_Spam.Command.Whitelist");

		if (config.getBoolean("Anti_Spam.Command.Block_Repetitive_Commands")) {
			if (!player.hasPermission("chatmanager.bypass.dupe.command")) {
				for (String commands : whitelistedCommands) {
					if (event.getMessage().contains(commands)) {
						plugin.api().getPreviousCmdData().removeUser(uuid);
						return;
					}
				}

				if (plugin.api().getPreviousCmdData().containsUser(uuid)) {
					String cmd = plugin.api().getPreviousCmdData().getMessage(uuid);
					if (command.equalsIgnoreCase(cmd)) {
						this.plugin.getMethods().sendMessage(player, messages.getString("Anti_Spam.Command.Repetitive_Message"), true);

						event.setCancelled(true);
					}
				}

				plugin.api().getPreviousCmdData().addUser(player.getUniqueId(), command);
			}

			if (config.getInt("Anti_Spam.Command.Command_Delay") != 0) {
				if (!player.hasPermission("chatmanager.bypass.commanddelay")) {
					if (plugin.api().getCmdCooldowns().containsUser(uuid)) {
						this.plugin.getMethods().sendMessage(player, messages.getString("Anti_Spam.Command.Delay_Message").replace("{Time}", String.valueOf(plugin.api().getCmdCooldowns().getTime(uuid))), true);
						event.setCancelled(true);
						return;
					}

					for (String commands : whitelistedCommands) {
						if (event.getMessage().contains(commands)) return;
					}

					plugin.api().getCmdCooldowns().addUser(uuid, config.getInt("Anti_Spam.Command.Command_Delay"));

					plugin.api().getCooldownTask().addUser(uuid, new BukkitRunnable() {
						@Override
						public void run() {
							int time = plugin.api().getCmdCooldowns().getTime(uuid);

							plugin.api().getCmdCooldowns().addUser(uuid, time--);

							if (time == 0) {
								plugin.api().getCmdCooldowns().removeUser(uuid);
								plugin.api().getCooldownTask().removeUser(uuid);
								cancel();
							}
						}
					});

					plugin.api().getCooldownTask().getUsers().get(player.getUniqueId()).runTaskTimer(plugin, 20L, 20L);
				}
			}
		}
	}
}