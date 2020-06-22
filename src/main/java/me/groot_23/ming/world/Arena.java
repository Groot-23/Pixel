package me.groot_23.ming.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.events.MGameJoinEvent;

public class Arena {
	
	private JavaPlugin plugin;
	private MiniGame game;
	
	private World world;
	private Location midSpawn;
	private String mapName;
	private int maxPlayers;
	private int minPlayers;
	private int midRadius;
	private int mapRadius;
	
	private boolean allowJoin = true;
	
	private List<Location> spawns;
	
	
	public Arena(MiniGame game, World world, String mapName) {
		this.game = game;
		this.world = world;
		this.mapName = mapName;
		this.plugin = game.getPlugin();
		readConfig();
		findPlayerSpawns();
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
	
	public int getMinPlayers() {
		return minPlayers;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public List<Location> getSpawns() {
		return spawns;
	}
	
	
	public void disableJoin() {
		if(allowJoin) {
			allowJoin = false;
			game.getArenaProvider().stopJoin(this);
		}
	}
	
	public boolean joinPlayer(Player player) {
		if(allowJoin) {
			player.teleport(midSpawn);
			
			MGameJoinEvent event = new MGameJoinEvent(player);
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			    @Override
			    public void run(){
			        player.setGameMode(GameMode.ADVENTURE);
			    }
			}, 1);
			if(world.getPlayers().size() >= maxPlayers) {
				disableJoin();
			}
			return true;
		}
		return false;
	}
	
	private void readConfig() {
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("worlds." + mapName);
		if(section == null) {
			throw new RuntimeException("Skywars Config does not contain '" + "worlds." + mapName + "'");
		}
		minPlayers = section.getInt("minPlayers");
		maxPlayers = section.getInt("maxPlayers");
		midRadius = section.getInt("midRadius");
		mapRadius = section.getInt("mapRadius");
		if(section.contains("spawns")) {
			int spawnX = section.getInt("spawns.x");
			int spawnY = section.getInt("spawns.y");
			int spawnZ = section.getInt("spawns.z");
			midSpawn = new Location(world, spawnX, spawnY, spawnZ);
		} else {
			midSpawn = world.getSpawnLocation();
		}
	}
	
	private void findPlayerSpawns() {
		spawns = new ArrayList<Location>();
		for(Entity entity : world.getEntities()) {
			if(entity.getType() == EntityType.ARMOR_STAND) {
				if(entity.getCustomName().equals("skywars_spawn")) {
					spawns.add(entity.getLocation());
				}
			}
		}
		Collections.shuffle(spawns);
	}
	
	public void removeArea(Location l1, Location l2) {
		int xLow  = Math.min(l1.getBlockX(), l2.getBlockX());
		int xHigh = Math.max(l1.getBlockX(), l2.getBlockX());
		int yLow  = Math.min(l1.getBlockY(), l2.getBlockY());
		int yHigh = Math.max(l1.getBlockY(), l2.getBlockY());
		int zLow  = Math.min(l1.getBlockZ(), l2.getBlockZ());
		int zHigh = Math.max(l1.getBlockZ(), l2.getBlockZ());
		
		for(int x = xLow; x <= xHigh; x++) {
			for(int y = yLow; y <= yHigh; y++) {
				for(int z = zLow; z <= zHigh; z++) {
					world.getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
	}
	
	public void removeArea(Location center, int width, int height) {
		int spawnX = midSpawn.getBlockX();
		int spawnY = midSpawn.getBlockY();
		int spawnZ = midSpawn.getBlockZ();
		for(int x = spawnX - width; x <= spawnX + width; x++) {
			for(int z = spawnZ - width; z <= spawnZ + width; z++) {
				for(int y = spawnY - height; y <= spawnY + height; y++) {
					world.getBlockAt(x, y, z).setType(Material.AIR);
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
