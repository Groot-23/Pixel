package me.groot_23.pixel.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.task.GameTaskManager;
import me.groot_23.pixel.player.PlayerUtil;
import me.groot_23.pixel.player.team.TeamHandler;
import me.groot_23.pixel.world.Arena;

public abstract class Game {
	
	public Arena arena;
	public final JavaPlugin plugin;
	
	public final String map;
	public final String name;
	
	public final GameTaskManager taskManager;
	
	public TeamHandler teamHandler;
	
	public final int id;
	private static int currentID = 0;

	
	public Game(String name, String map, JavaPlugin plugin) {
		this.id = currentID++;
		this.plugin = plugin;
		this.allowJoin = true;
		this.name = name;
		this.map = map;
		taskManager = new GameTaskManager();
	}
	
	public int getMaxPlayers() {return arena.getMaxPlayers();}
	
	protected boolean allowJoin;
	public final List<Player> players = new ArrayList<Player>();
	
	public final void joinPlayer(Player player) {
		if(allowJoin) {
			players.add(player);
			performJoin(player);
			onJoin(player);
		}
	}
	protected void performJoin(Player player) {
		arena.joinPlayer(player);
		if(players.size() >= arena.getMaxPlayers()) {
			allowJoin = false;
		}
	}
	
	public void stopJoin() {
		allowJoin = false;
		Pixel.GameProvider.stopJoin(this);
	}
	
	public void endGame() {
		Pixel.GameProvider.stopGame(this);
	}
	
	/*
	 * Event redirects
	 */
	
	public void onJoin(Player player) {}
	
	public void onDeath(PlayerDeathEvent event) {}
	
	public void onRespawn(PlayerRespawnEvent event) {}
	
	public void onPlayerLeave(Player player) {
		players.remove(player);
		teamHandler.removePlayer(player);
		PlayerUtil.resetPlayer(player);
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {}
	public void onBlockBreak(BlockBreakEvent event) {}
	
	public void onEntityDamage(EntityDamageEvent event) {}
	public void onEntityDeath(EntityDeathEvent event) {}
	
	public void onInteract(PlayerInteractEvent event) {}
	public void onInteractEntity(PlayerInteractEntityEvent event) {}
	public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {}
	
	public void onItemConsume(PlayerItemConsumeEvent event) {}
	
	public void onInventoryOpen(InventoryOpenEvent event) {}
	public void onInventoryClose(InventoryCloseEvent event) {}
	public void onInventoryClick(InventoryClickEvent event) {}
	
	/**
	 * Listener to prevent natural mob spawning in games. If want to allow natural spawning you will have to overwrite this method
	 */
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.getSpawnReason() == SpawnReason.NATURAL) {
			event.setCancelled(true);
		}
	}
	
	public void onEnd() {
		for(Player p : players) {
			PlayerUtil.resetPlayer(p);
		}
		Pixel.WorldProvider.removeWorld(arena.getWorld());
		taskManager.removeAllTasks();
	}
}
