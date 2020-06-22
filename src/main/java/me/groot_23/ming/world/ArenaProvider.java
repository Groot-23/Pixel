package me.groot_23.ming.world;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.game.Game;


public class ArenaProvider {
	
	private JavaPlugin plugin;
	private String worldPrefix;
	private MiniGame game;
	
	private Arena currentArena = null;
	
	private Map<String, Integer> weights;
	private String[] mapPickArray;
	private Random rand = new Random();
	
	private Map<UUID, Arena> arenaByUid = new HashMap<UUID, Arena>();
	
	public ArenaProvider(MiniGame game, Map<String, Integer> weights) {
		this.game = game;
		plugin = game.getPlugin();
		worldPrefix = game.getWorldPrefix();
		this.weights = weights;
		update();
	}
	
	public ArenaProvider(MiniGame game) {
		this.game = game;
		plugin = game.getPlugin();
		worldPrefix = game.getWorldPrefix();
		ConfigurationSection worlds = plugin.getConfig().getConfigurationSection("worlds");
		weights = new HashMap<String, Integer>();
		if (worlds != null) {
			for (String key : worlds.getKeys(false)) {
				int weight = worlds.getInt(key + ".weight");
				weights.put(key, weight);
			}
		}
		update();
	}
	
	public Arena getArenaById(UUID uid) {
		return arenaByUid.get(uid);
	}
	
	private void initWorldPickArray() {
		
		ArrayList<String> mapPickList = new ArrayList<String>();
		for(Map.Entry<String, Integer> entry : weights.entrySet()) {
			for(int i = 0; i < entry.getValue(); i++) {
				mapPickList.add(entry.getKey());
			}
		}
		mapPickArray = mapPickList.toArray(new String[mapPickList.size()]);

	}
	
	public void updateWeights(Map<String, Integer> newWeights) {
		this.weights = newWeights;
		initWorldPickArray();
	}
	
	public void clearWorlds() {
		// delete old worlds
		File[] oldWorlds = Bukkit.getWorldContainer().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(worldPrefix);
			}
		});
		for (File f : oldWorlds) {
			WorldUtil.deleteWorld(f.getName());
		}
	}
	
	public boolean createNewArena() {
		if(mapPickArray == null || mapPickArray.length == 0) return false;
		String currentMap = mapPickArray[rand.nextInt(mapPickArray.length)];
		System.out.println("[MinG] map for new arena: " + currentMap);
		currentArena = null;
		for (int i = 0; currentArena == null; i++) {
			String worldName = worldPrefix + currentMap + i;
			// try to reuse an already created world
			if (WorldUtil.worldExists(worldName)) {
				// check if the world has not been loaded yet
				if (Bukkit.getWorld(worldName) == null) {
					World world = Bukkit.createWorld(getWorldCreator(worldName));
					if(world != null) {
						currentArena = new Arena(game, world, currentMap);
					} else {
						System.out.println("[MinG] World " + worldName + " COULD NOT BE LOADED");
					}
				}
			// create a new world, if no old version could be found
			} else {
				WorldUtil.copyWorld(currentMap, worldName);
				World world = Bukkit.createWorld(getWorldCreator(worldName));
				// maybe no new world could be loaded for some reason -> prevent endless loop
				if(world != null) {
					currentArena = new Arena(game, world, currentMap);
				} else {
					return false;
				}
			}
		}

		new Game(game, currentArena).startGame();
		arenaByUid.put(currentArena.getWorld().getUID(), currentArena);

		System.out.println("[Skywars] Successfully created new arena: "+ currentArena.getWorld().getName());
		return true;
	}
	
	public void update() {
		initWorldPickArray();
		clearWorlds();
		createNewArena();
	}
	
	public Arena getArena() {
		if (currentArena == null) {
			createNewArena();
		}
		if(Bukkit.getWorld(currentArena.getWorld().getUID()) == null) {
			createNewArena();
		}
		currentArena.getWorld().setAutoSave(false);
		return currentArena;
	}
	
	public void joinPlayer(Player player) {
		currentArena = getArena();
		if(currentArena != null) {
			if(!currentArena.joinPlayer(player)) {
				createNewArena();
				currentArena.joinPlayer(player);
			}
		}
	}
	
	public void stopJoin(Arena arena) {
		if(arena.getWorld().getUID().equals(currentArena.getWorld().getUID())) {
			createNewArena();
		}
	}
	
	public void stopArena(World world) {
		arenaByUid.remove(world.getUID());
		WorldUtil.deleteWorld(world.getName());
	}
	
	public String[] getRegisteredWorlds() {
		Set<String> set = weights.keySet();
		return set.toArray(new String[set.size()]);
	}
	
	public WorldCreator getWorldCreator(String worldName) {
		WorldCreator creator = new WorldCreator(worldName);
		creator.generator(new ChunkGeneratorVoid());
		creator.generateStructures(false);
		creator.type(WorldType.FLAT);
		return creator;
	}
}
