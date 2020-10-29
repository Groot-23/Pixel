package me.groot_23.pixel;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.game.GameCreator;
import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.GuiRunnable;
import me.groot_23.pixel.gui.PixelGuiRunnables;
import me.groot_23.pixel.gui.GuiItem.UseAction;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.util.Tuple;
import me.groot_23.pixel.world.Arena;
import me.groot_23.pixel.world.ArenaCreator;
import me.groot_23.pixel.world.ChunkGeneratorVoid;
import me.groot_23.pixel.world.WorldUtil;

public class Pixel {

	private static JavaPlugin plugin;
	private static Map<UUID, Arena> arenas;
	private static Map<String, GuiRunnable> guiRunnables;
	private static Map<UUID, Boolean> spectators;
	private static int time;
	private static Random random = new Random();
	
	static void init(JavaPlugin plugin) {
		Pixel.plugin = plugin;
		arenas = new HashMap<UUID, Arena>();
		guiRunnables = new HashMap<String, GuiRunnable>();
		spectators = new HashMap<UUID, Boolean>();
		LanguageApi.defaultLanguage = plugin.getConfig().getString("default_language", "en_us");
		LanguageApi.addLanguageFolder(new File(plugin.getDataFolder(), "lang/minecraft"));
		LanguageApi.addLanguageFolder(new File(plugin.getDataFolder(), "lang/pixel"));
		WorldProvider.cleanWorldFolders();
		time = 0;
		new BukkitRunnable() {
			public void run() {
				++time;
			}
		}.runTaskTimer(plugin, 1, 1);
	}
	
	public static JavaPlugin getPlugin() {
		return plugin;
	}
	
	public static class WorldProvider {
		public static final String WORLD_PREFIX = "minigame_world_";

		public static void cleanWorldFolders() {
			for (String s : Bukkit.getWorldContainer().list()) {
				if (s.startsWith(WORLD_PREFIX)) {
					WorldUtil.deleteWorld(s);
				}
			}
		}

		public static World provideWorld(String name) {
			World world = null;
			if (WorldUtil.worldExists(name)) {
				for (int i = 0;; ++i) {
					String worldName = WORLD_PREFIX + name + i;
					if (Bukkit.getWorld(worldName) == null) {
						WorldUtil.copyWorld(name, worldName);
						world = Bukkit.createWorld(getWorldCreator(worldName));
						world.setAutoSave(false);
						return world;
					}
				}
			}
			return null;
		}

		public static Tuple<World, String> provideWorld(List<String> names) {
			String name = names.get(random.nextInt(names.size()));
			World world = provideWorld(name);
			return new Tuple<World, String>(world, name);
		}

		public static Arena provideArena(String name, Game game, ArenaCreator creator) {
			World world = provideWorld(name);
			if (world != null) {
				Arena arena = creator.createArena(game, world, name);
				arenas.put(arena.getWorld().getUID(), arena);
				return arena;
			}
			return null;
		}

		public static Arena provideArena(List<String> names, Game game, ArenaCreator creator) {
			Tuple<World, String> t = provideWorld(names);
			if (t != null) {
				Arena arena = creator.createArena(game, t.x, t.y);
				arenas.put(arena.getWorld().getUID(), arena);
				return arena;
			}
			return null;
		}
		
		public static void removeWorld(World world) {
			arenas.remove(world.getUID());
			WorldUtil.deleteWorld(world);
		}
		
		public static WorldCreator getWorldCreator(String worldName) {
			WorldCreator creator = new WorldCreator(worldName);
			creator.generator(new ChunkGeneratorVoid());
			creator.generateStructures(false);
			creator.type(WorldType.FLAT);
			return creator;
		}
	}
	
	public static Arena getArena(UUID id) {
		return arenas.get(id);
	}
	public static Game getGame(UUID id) {
		Arena arena = getArena(id);
		return arena != null ? arena.getGame() : null;
	}
	
	public static class GameProvider {
		public static final Map<String, Map<String, Game>> currentGames = new HashMap<String, Map<String,Game>>();
		private static Map<String, GameCreator> gameCreators = new HashMap<String, GameCreator>();
		
		public static enum ProvideType {
			
			RANDOM, MOST_PLAYERS, LEAST_PLAYERS;
			
			public String chooseOption(Map<String, Game> games) {
				if(this == RANDOM) {
					String[] options = games.keySet().toArray(new String[games.size()]);
					return options[random.nextInt(options.length)];
				} 
				else if(this == MOST_PLAYERS) {
					String option = null;
					int most = -1;
					for(Iterator<Map.Entry<String, Game>> it = games.entrySet().iterator(); it.hasNext();) {
						Map.Entry<String, Game> current = it.next();
						int players = (current.getValue() != null) ? current.getValue().players.size() : 0;
						if(players > most) {
							most = players;
							option = current.getKey();
						}
					}
					return option;
				}
				else if(this == LEAST_PLAYERS) {
					String option = null;
					int least = 1000;
					for(Iterator<Map.Entry<String, Game>> it = games.entrySet().iterator(); it.hasNext();) {
						Map.Entry<String, Game> current = it.next();
						int players = (current.getValue() != null) ? current.getValue().players.size() : 0;
						if(players < least) {
							least = players;
							option = current.getKey();
						}
					}
					return option;
				}
				// ERROR
				return null;
			}
		}

		public static Game provideGame(String game, String option) {
			if(!currentGames.containsKey(game)) {
				plugin.getLogger().warning("Can not provide the unregistered game '" + game + "'");
				return null;
			}
			Map<String, Game> games = currentGames.get(game);
			if(!games.containsKey(option)) {
				plugin.getLogger().warning("Can not provide the game '" + game + "' with unregistered option '" + option + "'");
				return null;
			}
			Game g = games.get(option);
			if(g == null) {
				g = gameCreators.get(game).createGame(option);
				games.put(option, g);
			}
			return g;
		}

		public static Game provideGame(String game, ProvideType type) {
			if(!currentGames.containsKey(game)) {
				plugin.getLogger().warning("Can not provide the unregistered game '" + game + "'");
				return null;
			}
			Map<String, Game> games = currentGames.get(game);
			if(games.size() == 0) {
				plugin.getLogger().warning("Can not provide the game '" + game + "' without any registered option");
				return null;
			}
			String option = type.chooseOption(games);
			Game g = games.get(option);
			if(g == null) {
				g = gameCreators.get(game).createGame(option);
				games.put(option, g);
			}
			return g;
		}
			
		public static void stopJoin(Game game) {
			Game current = currentGames.get(game.name).get(game.option);
			if(game == current) {
				currentGames.get(game.name).put(game.option, null);
			}
		}
		
		public static void stopGame(Game game) {
			stopJoin(game);
			game.onEnd();
		}
	}

	public static void registerGameOption(String game, String option) {
		if(!GameProvider.currentGames.containsKey(game)) {
			plugin.getLogger().warning("Can not register option for the unregistered game '" + game + "'");
			return;
		}
		GameProvider.currentGames.get(game).put(option, null);
	}
	
	public static void registerGame(String name, GameCreator creator) {
		GameProvider.gameCreators.put(name, creator);
		GameProvider.currentGames.put(name, new HashMap<String, Game>());
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
			setSpectatorInv(player);
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
			player.getInventory().clear();
			for(Player other : otherPlayers) {
				if(player == other) continue;
				if(Pixel.isSpectator(other)) player.hidePlayer(plugin, other);
				other.showPlayer(plugin, player);
			}
		}
	}
	public static boolean isSpectator(Player player) {
		Boolean b = spectators.get(player.getUniqueId());
		return b != null ? b : false;
	}
	
	public static void setSpectatorInv(Player player) {
		player.getInventory().clear();
		GuiItem tp = new GuiItem(Material.COMPASS);
		tp.addActionUseRunnable(PixelGuiRunnables.SPECTATOR_TP, UseAction.RIGHT_CLICK);
		player.getInventory().setItem(0, tp.getItem());
	}
	
	
	public static int getTime() {
		return time;
	}
 }
