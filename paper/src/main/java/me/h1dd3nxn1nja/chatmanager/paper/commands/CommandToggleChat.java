package me.h1dd3nxn1nja.chatmanager.paper.commands;

import com.ryderbelserion.chatmanager.paper.files.Files;
import me.h1dd3nxn1nja.chatmanager.paper.ChatManager;
import me.h1dd3nxn1nja.chatmanager.paper.managers.PlaceholderManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandToggleChat implements CommandExecutor {

	private final ChatManager plugin = ChatManager.getPlugin();
	private final PlaceholderManager placeholderManager = plugin.getCrazyManager().getPlaceholderManager();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player)) {
			this.plugin.getMethods().sendMessage(sender, "&cError: You can only use that command in-game", true);
			return true;
		}

		FileConfiguration messages = Files.MESSAGES.getFile();

		if (!cmd.getName().equalsIgnoreCase("toggleChat")) return true;

		if (!player.hasPermission("chatmanager.toggle.chat")) {
			this.plugin.getMethods().sendMessage(player, this.plugin.getMethods().noPermission(), true);
			return true;
		}

		if (args.length == 0) {
			if (plugin.api().getToggleChatData().containsUser(player.getUniqueId())) {
				plugin.api().getToggleChatData().removeUser(player.getUniqueId());
				this.plugin.getMethods().sendMessage(player, placeholderManager.setPlaceholders(player, messages.getString("Toggle_Chat.Disabled")), true);
				return true;
			}

			plugin.api().getToggleChatData().addUser(player.getUniqueId());
			this.plugin.getMethods().sendMessage(player, placeholderManager.setPlaceholders(player, messages.getString("Toggle_Chat.Enabled")), true);

			return true;
		}

		this.plugin.getMethods().sendMessage(player, "&cCommand Usage: &7/Togglechat", true);

		return true;
	}
}