package me.groot_23.ming.provider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import me.groot_23.ming.MinG;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.game.Game;
import me.groot_23.ming.util.Tuple;
import me.groot_23.ming.util.Utf8Config;
import me.groot_23.ming.world.Arena;
import me.groot_23.ming.world.ChunkGeneratorVoid;
import me.groot_23.ming.world.WorldUtil;

public class WorldProvider {

	protected MiniGame miniGame;
	protected Utf8Config config;

	public WorldProvider(MiniGame miniGame) {
		this.miniGame = miniGame;
		config = new Utf8Config();
		try {
			File file = getConfigFile();
			if (file.exists()) {
				config.load(file);
			} else {
				miniGame.getPlugin().getLogger().warning("[MinG] " + miniGame.getPlugin().getName()
						+ "/worlds.yml is missing! If this is your first time using this plugin you should"
						+ "/reload! Maybe the plugin will extract some resources.");
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void cleanWorldFolders() {
		for (String s : Bukkit.getWorldContainer().list()) {
			if (s.startsWith(miniGame.getWorldPrefix())) {
				WorldUtil.deleteWorld(s);
			}
		}
	}

	public World provideWorld(String name, int maxInstances) {
		World world = null;
		if (WorldUtil.worldExists(name)) {
			for (int i = 0; i < maxInstances; ++i) {
				String worldName = miniGame.getWorldPrefix() + name + i;
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

	public Tuple<World, String> provideWorld(String[] names, int maxInstances) {
		List<String> list = Arrays.asList(names);
		Collections.shuffle(list);
		for (String name : list) {
			World world = provideWorld(name, maxInstances);
			if (world != null)
				return new Tuple<World, String>(world, name);
		}
		return null;
	}

	public Arena provideArena(Game game, String name, int maxInstances) {
		World world = provideWorld(name, maxInstances);
		if (world != null) {
			Arena arena = game.createArena(world, name);
			MinG.registerArena(arena);
			return arena;
		}
		return null;
	}

	public Arena provideArena(Game game, String[] names, int maxInstances) {
		Tuple<World, String> t = provideWorld(names, maxInstances);
		if (t != null) {
			Arena arena = game.createArena(t.x, t.y);
			MinG.registerArena(arena);
			return arena;
		}
		return null;
	}

	public String[] getWorldGroup(String group) {
		List<String> strings = config.getStringList("groups." + group);
		return strings != null ? strings.toArray(new String[strings.size()]) : null;
	}

	public void addWorldToGroup(String world, String group) {
		List<String> strings = config.getStringList("groups." + group);
		if(!strings.contains(world)) {			
			strings.add(world);
			config.set("groups." + group, strings);
			if(!config.contains("worlds." + world)) {
				config.createSection("worlds." + world);
			}
			saveConfig();
		}
	}
	
	public void removeWorldFromGroup(String world, String group) {
		List<String> strings = config.getStringList("groups." + group);
		strings.remove(world);
		config.set("groups." + group, strings);
		saveConfig();
	}

	public ConfigurationSection getWorldSection(String world) {
		ConfigurationSection sec = config.getConfigurationSection("worlds." + world);
		if (sec == null)
			config.createSection("worlds." + world);
		return sec;
	}

	public Utf8Config getConfig() {
		return config;
	}

	public File getConfigFile() {
		return new File(miniGame.getPlugin().getDataFolder(), "worlds.yml");
	}

	public void saveConfig() {
		try {
			config.save(getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void removeWorld(World world) {
		MinG.removeArena(world.getUID());
		WorldUtil.deleteWorld(world);
	}

	public WorldCreator getWorldCreator(String worldName) {
		WorldCreator creator = new WorldCreator(worldName);
		creator.generator(new ChunkGeneratorVoid());
		creator.generateStructures(false);
		creator.type(WorldType.FLAT);
		return creator;
	}
}
