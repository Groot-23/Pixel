package me.groot_23.ming.world;

import java.util.Arrays;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.game.Game;

public class Arena {

	protected JavaPlugin plugin;
	protected Game game;

	protected World world;
	protected Location midSpawn;
	protected String mapName;
	protected String builder;
	protected int maxPlayers;
	protected int minPlayers;
	protected int midRadius;
	protected int mapRadius;


	public Arena(Game game, World world, String mapName) {
		this.game = game;
		this.world = world;
		this.mapName = mapName;
		this.plugin = game.mode.getPlugin();
		readConfig();
	}

	public Game getGame() {
		return game;
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

	public int getMinPlayers() {
		return minPlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
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

	protected void readConfig() {
		ConfigurationSection section = game.miniGame.worldProvider.getWorldSection(mapName); // plugin.getConfig().getConfigurationSection("worlds." + mapName);
		if (section == null) {
			throw new RuntimeException("[MinG] " + plugin.getName() + "config does not contain '" + "worlds." + mapName + "'");
		}
		builder = section.getString("builder");
		minPlayers = section.getInt("minPlayers");
		maxPlayers = section.getInt("maxPlayers");
		midRadius = section.getInt("midRadius");
		mapRadius = section.getInt("mapRadius");
		if (section.contains("spawns")) {
			int spawnX = section.getInt("midSpawn.x");
			int spawnY = section.getInt("midSpawn.y");
			int spawnZ = section.getInt("midSpawn.z");
			midSpawn = new Location(world, spawnX, spawnY, spawnZ);
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
