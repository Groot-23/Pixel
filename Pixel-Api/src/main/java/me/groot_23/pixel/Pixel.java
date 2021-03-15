package me.groot_23.pixel;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.pixel.display.JoinSignApi;
import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.game.Lobby;
import me.groot_23.pixel.game.LobbyCreator;
import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.GuiItem.UseAction;
import me.groot_23.pixel.gui.runnables.SpectatorTpRunnable;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.util.Tuple;
import me.groot_23.pixel.world.ChunkGeneratorVoid;
import me.groot_23.pixel.world.GameWorld;
import me.groot_23.pixel.world.LobbyWorld;
import me.groot_23.pixel.world.WorldUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Pixel {

	private static JavaPlugin plugin;
	public static Map<UUID, GameWorld> gameWorlds = new HashMap<UUID, GameWorld>();
	public static Map<UUID, LobbyWorld> lobbyWorlds = new HashMap<UUID, LobbyWorld>();
	private static Map<UUID, Boolean> spectators = new HashMap<UUID, Boolean>();
	private static int time;
	private static Random random = new Random();
	private static String lobbyCmd;
	
	static void init(JavaPlugin plugin) {
		Pixel.plugin = plugin;
		LanguageApi.defaultLanguage = plugin.getConfig().getString("default_language", "en_us");
		LanguageApi.addLanguageFolder(new File(plugin.getDataFolder(), "lang/minecraft"));
		LanguageApi.addLanguageFolder(new File(plugin.getDataFolder(), "lang/pixel"));
		lobbyCmd = plugin.getConfig().getString("lobby", "lobby");
		time = 0;
		new BukkitRunnable() {
			public void run() {
				++time;
			}
		}.runTaskTimer(plugin, 1, 1);
		
		JoinSignApi.init();
		new BukkitRunnable() {
			public void run() {
				JoinSignApi.updateSigns();
			}
		}.runTaskTimer(plugin, 100, 10);
		
		WorldProvider.cleanWorldFolders();
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
		
		/**
		 * Attempts to delete the world file. ONLY CALL IT FOR minigame_worlds that were created by copying the world file.
		 */
		public static void removeWorld(World world) {
			gameWorlds.remove(world.getUID());
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
	
	public static LobbyWorld getLobby(UUID id) {
		return lobbyWorlds.get(id);
	}
	public static GameWorld getArena(UUID id) {
		return gameWorlds.get(id);
	}
	public static Game getGame(UUID id) {
		GameWorld arena = getArena(id);
		return arena != null ? arena.game : null;
	}
	
	public static class LobbyProvider {
		public static final Map<String, Map<String, Lobby>> currentlobbies = new HashMap<String, Map<String,Lobby>>();
		private static Map<String, LobbyCreator> gameCreators = new HashMap<String, LobbyCreator>();
		private static Map<String, Map<String, Integer>> maxPlayers = new HashMap<String, Map<String,Integer>>();
		
		public static int getPlayerCount(String game, String map) {
			Lobby lobby = currentlobbies.get(game).get(map);
			return lobby == null ? 0 : lobby.players.size();
		}
		public static int getMaxPlayers(String game, String map) {
			Integer i = maxPlayers.get(game).get(map);
			return i == null ? 0 : i;
		}
		
		public static enum ProvideType {
			
			RANDOM, MOST_PLAYERS, LEAST_PLAYERS;
			
			public String chooseOption(Map<String, Lobby> lobbies) {
				if(this == RANDOM) {
					String[] options = lobbies.keySet().toArray(new String[lobbies.size()]);
					return options[random.nextInt(options.length)];
				} 
				else if(this == MOST_PLAYERS) {
					String option = null;
					int most = -1;
					for(Iterator<Map.Entry<String, Lobby>> it = lobbies.entrySet().iterator(); it.hasNext();) {
						Map.Entry<String, Lobby> current = it.next();
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
					for(Iterator<Map.Entry<String, Lobby>> it = lobbies.entrySet().iterator(); it.hasNext();) {
						Map.Entry<String, Lobby> current = it.next();
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

		public static Lobby provideLobby(String game, String map) {
			if(!currentlobbies.containsKey(game)) {
				plugin.getLogger().warning("Can not provide the unregistered game '" + game + "'");
				return null;
			}
			Map<String, Lobby> lobbies = currentlobbies.get(game);
			if(!lobbies.containsKey(map)) {
				plugin.getLogger().warning("Can not provide the game '" + game + "' with unregistered map '" + map + "'");
				return null;
			}
			Lobby lobby = lobbies.get(map);
			if(lobby == null) {
				lobby = gameCreators.get(game).createGame(map);
				lobbies.put(map, lobby);
			}
			return lobby;
		}

		public static Lobby provideGame(String game, ProvideType type) {
			if(!currentlobbies.containsKey(game)) {
				plugin.getLogger().warning("Can not provide the unregistered game '" + game + "'");
				return null;
			}
			Map<String, Lobby> lobbies = currentlobbies.get(game);
			if(lobbies.size() == 0) {
				plugin.getLogger().warning("Can not provide the game '" + game + "' without any registered map");
				return null;
			}
			String map = type.chooseOption(lobbies);
			Lobby lobby = lobbies.get(map);
			if(lobby == null) {
				lobby = gameCreators.get(game).createGame(map);
				lobbies.put(map, lobby);
			}
			return lobby;
		}
			
		public static void stopLobby(Lobby lobby) {
			lobbyWorlds.remove(lobby.world.getWorld().getUID());
			Lobby current = currentlobbies.get(lobby.game).get(lobby.map);
			if(lobby == current) {
				currentlobbies.get(lobby.game).put(lobby.map, null);
			}
		}
	}

	public static void registerGameMap(String game, String map, int maxPlayers) {
		if(!LobbyProvider.currentlobbies.containsKey(game)) {
			plugin.getLogger().warning("Can not register map for the unregistered game '" + game + "'");
			return;
		}
		LobbyProvider.currentlobbies.get(game).put(map, null);
		LobbyProvider.maxPlayers.get(game).put(map, maxPlayers);
	}
	
	public static void registerGame(String name, LobbyCreator creator) {
		LobbyProvider.gameCreators.put(name, creator);
		LobbyProvider.currentlobbies.put(name, new HashMap<String, Lobby>());
		LobbyProvider.maxPlayers.put(name, new HashMap<String, Integer>());
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
			player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR);
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
		tp.addUseRunnable(new SpectatorTpRunnable(), UseAction.RIGHT_CLICK);
		player.getInventory().setItem(0, tp.getItem());
	}
	
	
	public static int getTime() {
		return time;
	}
	
	
	
	/**
	 * Teleport player to lobby. Lobby Command has to be set in Pixel's config (lobby: "myCmd")
	 */
	public static void lobby(Player player) {
		player.performCommand(lobbyCmd);
	}
	
	/*
	 * Vault wrapper:
	 * Hook into Vault when needed and NOT on startup! This allows plugins which are 
	 * currently not supported by Vault to add support for it and get rid of loading issues.
	 */
	
	private static Economy economy = null;
	private static Chat chat = null;
	private static Permission permission = null;
	
	public static Economy getEconomy() {
		if(economy == null) {
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
			if(rsp != null) {
				economy = rsp.getProvider();
			}
		}
		return economy;
	}
	
	public static Chat getChat() {
		if(chat == null) {
			RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);
			if(rsp != null) {
				chat = rsp.getProvider();
			}
		}
		return chat;
	}
	
	public static Permission getPermission() {
		if(permission == null) {
			RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
			if(rsp != null) {
				permission = rsp.getProvider();
			}
		}
		return permission;
	}
 }
