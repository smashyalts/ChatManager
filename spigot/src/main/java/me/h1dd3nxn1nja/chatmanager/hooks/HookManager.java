package me.h1dd3nxn1nja.chatmanager.hooks;

import me.h1dd3nxn1nja.chatmanager.ChatManager;
import me.h1dd3nxn1nja.chatmanager.Methods;

public class HookManager {

	private final ChatManager plugin = ChatManager.getPlugin();

	protected static PlaceholderAPIHook placeholderApi;
	protected static VaultHook vault;
	protected static ProtocolLibHook protocolLib;
	protected static SuperVanishHook superVanish;
	protected static SuperVanishHook premiumVanish;
	protected static EssentialsHook essentials;

	public HookManager() {}

	public void hookManager() {
		if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) placeholderApi = new PlaceholderAPIHook();
	}

	public static void loadDependencies() {

		if (Methods.doesPluginExist("PlaceholderAPI")) {
			placeholderApi = new PlaceholderAPIHook();
			new PlaceholderAPIHook().register();
		}

		if (Methods.doesPluginExist("Essentials")) essentials = new EssentialsHook();

		if (Methods.doesPluginExist("Vault")) vault = new VaultHook();

		if (Methods.doesPluginExist("SuperVanish")) superVanish = new SuperVanishHook();
		
		if (Methods.doesPluginExist("PremiumVanish")) premiumVanish = new SuperVanishHook();
	}

	public static boolean isEssentialsLoaded() {
		return essentials != null;
	}

	public static boolean isSuperVanishLoaded() {
		return superVanish != null;
	}
	
	public static boolean isPremiumVanishLoaded() {
		return premiumVanish != null;
	}

	public static boolean isPlaceholderAPILoaded() {
		return placeholderApi != null;
	}
	
	public static boolean isProtocolLibLoaded() {
		return protocolLib != null;
	}

	public static boolean isVaultLoaded() {
		return vault != null;
	}
}