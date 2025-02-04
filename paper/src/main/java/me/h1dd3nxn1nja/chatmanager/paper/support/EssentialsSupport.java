package me.h1dd3nxn1nja.chatmanager.paper.support;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.entity.Player;

public class EssentialsSupport {

	private Essentials essentials = null;

	public EssentialsSupport() {
		if (PluginSupport.ESSENTIALS.isPluginEnabled()) essentials = (Essentials) PluginSupport.ESSENTIALS.getPlugin();
	}

	public boolean isPlayerAFK(String player) {
		if (isEssentialsReady()) return false;

		User user = getUser(player);

		return user != null && user.isAfk();
	}
	
	public boolean isIgnored(Player player, Player player2) {
		if (isEssentialsReady()) return false;

		User user = this.essentials.getUser(player);

		if (user == null) return false;

		User user2 = this.essentials.getUser(player2);

        return user2 != null && user.isIgnoredPlayer(user2);
	}
	
	public String getPlayerNickname(Player player) {
		if (isEssentialsReady()) return "Essentials is not enabled.";

		User user = this.essentials.getUser(player);

		if (user == null) return null;

		if (user.getNickname() != null) return user.getNickname();

		return user.getName();
	}
	
	public String getPlayerBalance(Player player) {
		if (isEssentialsReady()) return "0";

		User user = essentials.getUser(player);

		if (user == null) return "0";

		String money = String.valueOf(user.getMoney());

		if (money == null) return "0";

		return money;
	}
	
	public boolean isMuted(Player player) {
		if (isEssentialsReady()) return false;

		User user = essentials.getUser(player);

		if (user == null) return false;

		return user.isMuted();
	}

	public User getUser(String player) {
		return essentials.getUserMap().getUser(player);
	}

	public User getUser(Player player) {
		return essentials.getUser(player);
	}

	public boolean isEssentialsReady() {
		return essentials == null;
	}

	public Essentials getEssentials() {
		return essentials;
	}
}