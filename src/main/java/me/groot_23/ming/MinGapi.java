package me.groot_23.ming;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.listener.GuiListener;

public class MinGapi {
	
	private static Map<String, MiniGame> miniGames;
	
	private static boolean initialized = false;
	
	static {
		miniGames = new HashMap<String, MiniGame>();
	}
	
	public static void test(Player player) {
		player.sendMessage("Hello from MinG. This is another test!. 33333333");
	}
	
	private static void registerListeners(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
	}
	
	public static void registerMiniGame(MiniGame miniGame) {
		registerListeners(miniGame.getPlugin());
		miniGames.put(miniGame.getName(), miniGame);
	}
	
	public static MiniGame getMiniGame(String name) {
		return miniGames.get(name);
	}
	
	public static void init() {
		if(!initialized) {

			initialized = true;
		}
	}
	
	public static void guiExecute(String game, String cmd, Player player, ItemStack item, Inventory inv) {
		MiniGame mg = miniGames.get(game);
		if(game != null) {
			mg.guiExecute(cmd, player, item, inv);
		} else {
			System.out.println("[MinG] The minigame '" + game + "' is not registered!");
			System.out.println("[MinG] This may happen when multiple minigames use their own copy of this api");
			System.out.println("[MinG] Don't worry about this if everything works in game");
		}
	}
	
}
