package me.h1dd3nxn1nja.chatmanager.paper.listeners;

import com.ryderbelserion.chatmanager.paper.files.Files;
import me.h1dd3nxn1nja.chatmanager.paper.ChatManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class ListenerAntiBot implements Listener {

	private final ChatManager plugin = ChatManager.getPlugin();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		FileConfiguration config = Files.CONFIG.getFile();

		if (!config.getBoolean("Anti_Bot.Block_Chat_Until_Moved") || player.hasPermission("chatmanager.bypass.antibot")) return;

		plugin.api().getAntiBotData().addUser(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		FileConfiguration config = Files.CONFIG.getFile();

		if (!config.getBoolean("Anti_Bot.Block_Chat_Until_Moved") || player.hasPermission("chatmanager.bypass.antibot")) return;

		plugin.api().getAntiBotData().removeUser(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		FileConfiguration config = Files.CONFIG.getFile();

		if (!config.getBoolean("Anti_Bot.Block_Chat_Until_Moved") || player.hasPermission("chatmanager.bypass.antibot")) return;

		plugin.api().getAntiBotData().removeUser(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		FileConfiguration config = Files.CONFIG.getFile();
		FileConfiguration messages = Files.MESSAGES.getFile();

		if (!config.getBoolean("Anti_Bot.Block_Chat_Until_Moved") || player.hasPermission("chatmanager.bypass.antibot")) return;

		if (!plugin.api().getAntiBotData().containsUser(player.getUniqueId())) return;

		event.setCancelled(true);

		this.plugin.getMethods().sendMessage(player, messages.getString("Anti_Bot.Deny_Chat_Message"), true);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();

		FileConfiguration config = Files.CONFIG.getFile();
		FileConfiguration messages = Files.MESSAGES.getFile();

		if (!config.getBoolean("Anti_Bot.Block_Commands_Until_Moved") || player.hasPermission("chatmanager.bypass.antibot")) return;

		if (!plugin.api().getAntiBotData().containsUser(player.getUniqueId())) return;

		event.setCancelled(true);
		this.plugin.getMethods().sendMessage(player, messages.getString("Anti_Bot.Deny_Command_Message"), true);
	}
}