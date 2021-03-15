package me.groot_23.pixel.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.display.BossBarApi;

public class PlayerUtil {

	public static void hideFromWorld(Player player) {
		for (Player other : player.getWorld().getPlayers()) {
			if (player == other)
				continue;
			other.hidePlayer(Pixel.getPlugin(), player);
		}
	}

	public static void hideFromAll(Player player) {
		for (Player other : Bukkit.getServer().getOnlinePlayers()) {
			if (player == other)
				continue;
			other.hidePlayer(Pixel.getPlugin(), player);
		}
	}

	public static void showToWorld(Player player) {
		for (Player other : player.getWorld().getPlayers()) {
			if (player == other)
				continue;
			other.showPlayer(Pixel.getPlugin(), player);
		}
	}

	public static void showToAll(Player player) {
		for (Player other : Bukkit.getServer().getOnlinePlayers()) {
			if (player == other)
				continue;
			other.showPlayer(Pixel.getPlugin(), player);
		}
	}

	private static Attribute[] playerAttributes = { Attribute.GENERIC_ARMOR, Attribute.GENERIC_ARMOR_TOUGHNESS,
			Attribute.GENERIC_ATTACK_DAMAGE, Attribute.GENERIC_ATTACK_SPEED, Attribute.GENERIC_KNOCKBACK_RESISTANCE,
			Attribute.GENERIC_LUCK, Attribute.GENERIC_MAX_HEALTH, Attribute.GENERIC_MOVEMENT_SPEED };

	
	public static void clear(Player player) {
		player.getInventory().clear();
		
		player.setAbsorptionAmount(0);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
//			player.addPotionEffect(new PotionEffect(effect.getType(), 1, 0, false, false));
		}
		for(Attribute att : playerAttributes) {
			AttributeInstance instance = player.getAttribute(att);
			for(AttributeModifier mod : instance.getModifiers()) {
				instance.removeModifier(mod);
			}
		}
	}
	
	/**
	 * Resets attribute data of player (effects, inventory, food, ...), but keeps plugin data (team, spectator...)
	 */
	public static void resetPlayerMC(Player player) {
		clear(player);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(5);
		player.setExhaustion(0);
		player.resetTitle();
		player.setLevel(0);
		player.setFireTicks(0);
	}
	
	/**
	 * Reset mcdata + plugin data
	 */
	public static void resetPlayer(Player player) {
		resetPlayerMC(player);
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		BossBarApi.removePlayer(player);
		player.removeMetadata("pixel_team", Pixel.getPlugin());
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
