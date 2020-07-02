package me.groot_23.ming;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import me.groot_23.ming.display.BossBarManager;
import me.groot_23.ming.listener.GuiListener;

public class MinG {
	
	private static Map<String, MiniGame> miniGames;
	
	private static boolean initialized = false;
	
	static {
		miniGames = new HashMap<String, MiniGame>();
	}
	
	private static void registerListeners(JavaPlugin plugin) {
		Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
	}
	
	public static void registerMiniGame(MiniGame miniGame) {
		init(miniGame.getPlugin());
		miniGames.put(miniGame.getName(), miniGame);
	}
	
	public static MiniGame getMiniGame(String name) {
		return miniGames.get(name);
	}
	
	// e.g. listeners should only be registered once to prevent bugs caused by double executions
	// so only register them once!
	private static void init(JavaPlugin plugin) {
		if(!initialized) {
			registerListeners(plugin);
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
	
	
	public static void resetPlayer(Player player) {
		player.getInventory().clear();
		for(PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(5);
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		player.setLevel(0);
		BossBarManager.removePlayer(player);
		player.setDisplayName(player.getName());
	}
	
}
