package me.groot_23.pixel.world;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.util.Utf8Config;

public class PixelWorld {

	protected JavaPlugin plugin;
	protected Utf8Config config;

	protected World world;
	protected Location midSpawn;
	protected String mapName;
	protected String builder;
	protected int midRadius;
	protected int mapRadius;

	public PixelWorld(JavaPlugin plugin, World world, String mapName) {
		this.world = world;
		this.mapName = mapName;
		this.plugin = plugin;
		readConfig();
	}
	
	public PixelWorld(PixelWorld pworld) {
		this.world = pworld.world;
		this.mapName = pworld.mapName;
		this.plugin = pworld.plugin;
		this.config = pworld.config;
		this.midSpawn = pworld.midSpawn;
		this.builder = pworld.builder;
		this.midRadius = pworld.midRadius;
		this.mapRadius = pworld.mapRadius;
	}

	public World getWorld() {
		return world;
	}

	public Location getMidSpawn() {
		return midSpawn;
	}

	public String getMapName() {
		return mapName;
	}
	
	public String getBuilder() {
		return builder;
	}


	public void joinPlayer(Player player) {
		player.teleport(midSpawn);
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				player.setGameMode(GameMode.ADVENTURE);
			}
		}, 1);
	}

	public File getConfigFile() {
		return new File(plugin.getDataFolder(), "worlds.yml");
	}
	
	public ConfigurationSection getConfig() {
		if(config == null) {
			config = new Utf8Config();
			try {
				config.load(getConfigFile());
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		ConfigurationSection sec = config.getConfigurationSection(mapName);
		return sec != null ? sec : config.createSection(mapName);
	}
	
	public void saveConfig() {
		try {
			config.save(getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void readConfig() {
		ConfigurationSection section = getConfig();
		if (section == null) {
			throw new RuntimeException(plugin.getName() + "world config does not contain '" + mapName + "'");
		}
		builder = section.getString("builder");
		midRadius = section.getInt("midRadius");
		mapRadius = section.getInt("mapRadius");
		if (section.contains("midSpawn")) {
			int spawnX = section.getInt("midSpawn.x");
			int spawnY = section.getInt("midSpawn.y");
			int spawnZ = section.getInt("midSpawn.z");
			midSpawn = new Location(world, (double)spawnX + 0.5, spawnY, (double)spawnZ + 0.5);
		} else {
			midSpawn = world.getSpawnLocation();
		}
	}



	public void removeArea(Location l1, Location l2, Material... filter) {
		HashSet<Material> filterSet = new HashSet<Material>(Arrays.asList(filter));

		int xLow  = Math.min(l1.getBlockX(), l2.getBlockX());
		int xHigh = Math.max(l1.getBlockX(), l2.getBlockX());
		int yLow  = Math.min(l1.getBlockY(), l2.getBlockY());
		int yHigh = Math.max(l1.getBlockY(), l2.getBlockY());
		int zLow  = Math.min(l1.getBlockZ(), l2.getBlockZ());
		int zHigh = Math.max(l1.getBlockZ(), l2.getBlockZ());

		for (int x = xLow; x <= xHigh; x++) {
			for (int y = yLow; y <= yHigh; y++) {
				for (int z = zLow; z <= zHigh; z++) {
					Block b = world.getBlockAt(x, y, z);
					if (filterSet.isEmpty() || filterSet.contains(b.getType())) {
						b.setType(Material.AIR);
					}
				}
			}
		}
	}

	public void removeArea(Location center, int width, int height, Material... filter) {
		HashSet<Material> filterSet = new HashSet<Material>(Arrays.asList(filter));

		int spawnX = center.getBlockX();
		int spawnY = center.getBlockY();
		int spawnZ = center.getBlockZ();
		for (int x = spawnX - width; x <= spawnX + width; x++) {
			for (int z = spawnZ - width; z <= spawnZ + width; z++) {
				for (int y = spawnY - height; y <= spawnY + height; y++) {
					Block b = world.getBlockAt(x, y, z);
					if (filterSet.isEmpty() || filterSet.contains(b.getType())) {
						b.setType(Material.AIR);
					}
				}
			}
		}
	}

	public void initBorder() {
		world.getWorldBorder().setCenter(midSpawn);
		world.getWorldBorder().setSize(2 * mapRadius);
		world.getWorldBorder().setDamageBuffer(0);
	}

	public void shrinkBorder(int seconds) {
		world.getWorldBorder().setSize(2 * midRadius, seconds);
	}

	public boolean isInsideMidSpawn(Location location) {
		int xDist = midSpawn.getBlockX() - location.getBlockX();
		int zDist = midSpawn.getBlockZ() - location.getBlockZ();
		return (Math.abs(xDist) < midRadius && Math.abs(zDist) < midRadius);
	}
	
}
