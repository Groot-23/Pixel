package me.groot_23.pixel.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.display.BossBarApi;

public class PlayerUtil {
	
	public static void hideFromWorld(Player player) {
		for(Player other : player.getWorld().getPlayers()) {
			if(player == other) continue;
			other.hidePlayer(Pixel.getPlugin(), player);
		}
	}
	public static void hideFromAll(Player player) {
		for(Player other : Bukkit.getServer().getOnlinePlayers()) {
			if(player == other) continue;
			other.hidePlayer(Pixel.getPlugin(), player);
		}
	}
	public static void showToWorld(Player player) {
		for(Player other : player.getWorld().getPlayers()) {
			if(player == other) continue;
			other.showPlayer(Pixel.getPlugin(), player);
		}
	}
	public static void showToAll(Player player) {
		for(Player other : Bukkit.getServer().getOnlinePlayers()) {
			if(player == other) continue;
			other.showPlayer(Pixel.getPlugin(), player);
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
		BossBarApi.removePlayer(player);
		player.removeMetadata("ming_team", Pixel.getPlugin());
		player.setFireTicks(0);
		Pixel.setSpectator(player, false);
		showToWorld(player);
		setLastAttacker(player, null);
	}
	
	private static Map<UUID, Player> lastAttacker = new HashMap<UUID, Player>();
	
	public static void setLastAttacker(Player player, Player attacker) {
		lastAttacker.put(player.getUniqueId(), attacker);
	}
	public static Player getLastAttacker(Player player) {
		return lastAttacker.get(player.getUniqueId());
	}
}
