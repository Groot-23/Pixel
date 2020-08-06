package me.groot_23.ming;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.ming.game.Game;
import me.groot_23.ming.gui.GuiItem;
import me.groot_23.ming.gui.GuiRunnable;
import me.groot_23.ming.kits.Kit;
import me.groot_23.ming.language.LanguageManager;
import me.groot_23.ming.util.Utf8Config;
import me.groot_23.ming.world.Arena;

public class MinG {

	private static JavaPlugin plugin;
	private static Map<UUID, Arena> arenas;
	private static Map<String, GuiRunnable> guiRunnables;
	private static Map<UUID, Boolean> spectators;
	private static Map<String, List<Kit>> kits;
	private static Map<UUID, Map<String, String>> selectedKits;
	private static String defaultLanguage;
	private static LanguageManager langManager;
	private static int time;
	
	static void init(JavaPlugin plugin) {
		MinG.plugin = plugin;
		arenas = new HashMap<UUID, Arena>();
		guiRunnables = new HashMap<String, GuiRunnable>();
		spectators = new HashMap<UUID, Boolean>();
		kits = new HashMap<String, List<Kit>>();
		selectedKits = new HashMap<UUID, Map<String,String>>();
		defaultLanguage = plugin.getConfig().getString("default_language", "en_us");
		langManager = new LanguageManager(defaultLanguage);
		langManager.addLanguageHolder(new File(plugin.getDataFolder(), "lang"));
		time = 0;
		new BukkitRunnable() {
			public void run() {
				++time;
			}
		}.runTaskTimer(plugin, 1, 1);
	}
	
	public static void registerArena(Arena arena) {
		arenas.put(arena.getWorld().getUID(), arena);
	}
	public static void removeArena(UUID id) {
		arenas.remove(id);
	}
	public static Arena getArena(UUID id) {
		return arenas.get(id);
	}
	public static Game getGame(UUID id) {
		Arena arena = getArena(id);
		return arena != null ? arena.getGame() : null;
	}
	
	public static void registerGuiRunnable(String name, GuiRunnable runnable) {
		guiRunnables.put(name, runnable);
	}
	public static void guiExecute(String command, Player player, ItemStack item, Inventory inventory) {
		if(command.startsWith(GuiItem.GUI_RUNNABLE_PREFIX)) {
			command = command.substring(GuiItem.GUI_RUNNABLE_PREFIX.length());
			GuiRunnable runnable = guiRunnables.get(command);
			if(runnable == null) {
				throw new IllegalArgumentException("The guiRunnable: '" + command + "' is not registered!");
			} else {
				runnable.run(player, item, inventory);
			}
		} else {
			player.performCommand(command);
		}
	}
	
	public static void setSpectator(Player player, boolean value) {
		spectators.put(player.getUniqueId(), value);
		Game game = getGame(player.getWorld().getUID());
		Collection<Player> otherPlayers = game != null ? game.players : player.getWorld().getPlayers();
		if(value) {
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setCollidable(false);
			player.setInvulnerable(true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 10000, 0, false, false, false));
			for(Player other : otherPlayers) {
				if(player == other) continue;
				if(isSpectator(other)) {
					other.showPlayer(plugin, player);
					player.showPlayer(plugin, other);
				} else {
					other.hidePlayer(plugin, player);
				}
			}
		} else {
			player.setAllowFlight(false);
			player.setCollidable(true);
			player.setInvulnerable(false);
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			for(Player other : otherPlayers) {
				if(player == other) continue;
				other.showPlayer(plugin, player);
			}
		}
	}
	public static boolean isSpectator(Player player) {
		Boolean b = spectators.get(player.getUniqueId());
		return b != null ? b : false;
	}
	
	
	public static void registerKit(Kit kit, String kitGroup) {
		List<Kit> list = kits.get(kitGroup);
		if(list == null) {
			list = new ArrayList<Kit>();
			kits.put(kitGroup, list);
		}
		list.add(kit);
	}
	public static Kit getKit(String group, String name) {
		List<Kit> list = kits.get(group);
		if(list != null) {
			for(Kit kit : list) {
				if(kit.getName().equals(name)) {
					return kit;
				}
			}
		}
		return null;
	}
	public static String getSelectedKitName(Player player, String group) {
		Map<String, String> playerSection = selectedKits.get(player.getUniqueId());
		if(playerSection != null) {
			String name = playerSection.get(group);
			if(name != null) {
				return name;
			}
		}
		return null;
	}
	public static Kit getSelectedKit(Player player, String group) {
		String name = getSelectedKitName(player, group);
		return name != null ? getKit(group, name) : null;
	}
	public static void setSelectedKit(Player player, String group, String kit) {
		Map<String, String> playerSection = selectedKits.get(player.getUniqueId());
		if(playerSection == null) {
			playerSection = new HashMap<String, String>();
			selectedKits.put(player.getUniqueId(), playerSection);
		}
		playerSection.put(group, kit);
	}
	public static void loadKits(File kitFile, String kitGroup) {
		Utf8Config cfg = new Utf8Config();
		try {
			cfg.load(kitFile);
			for(String key : cfg.getKeys(false)) {
				ConfigurationSection kitSec = cfg.getConfigurationSection(key);
				if(kitSec != null) {
					Kit.loadKit(kitGroup, kitSec);
				}
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	public static List<Kit> getKits(String group) {
		return kits.get(group);
	}
	
	public static LanguageManager getLanguageManager() {
		return langManager;
	}
	public static String getDefaultLanguage() {
		return defaultLanguage;
	}
	
	public static int getTime() {
		return time;
	}
 }
