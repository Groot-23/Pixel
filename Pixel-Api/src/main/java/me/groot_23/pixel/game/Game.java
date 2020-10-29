package me.groot_23.pixel.game;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
	
	public final String option;
	public final String name;
	
	public final GameTaskManager taskManager;
	
	public TeamHandler teamHandler;
	
	public final int id;
	private static int currentID = 0;

	
	public Game(String name, String option, JavaPlugin plugin, int teamSize) {
		this.id = currentID++;
		this.plugin = plugin;
		this.allowJoin = true;
		this.name = name;
		this.option = option;
		taskManager = new GameTaskManager();
	}
	
	
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
	
	public void onJoin(Player player) {}
	
	public void onDeath(PlayerDeathEvent event) {}
	
	public void onRespawn(PlayerRespawnEvent event) {}
	
	public void onPlayerLeave(Player player) {
		players.remove(player);
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {}
	public void onBlockBreak(BlockBreakEvent event) {}
	
	public void onEntityDamage(EntityDamageEvent event) {}
	public void onEntityDeath(EntityDeathEvent event) {}
	
	public void onInteract(PlayerInteractEvent event) {}
	public void onInteractEntity(PlayerInteractEntityEvent event) {}
	public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {}
	
	public void onEnd() {
		for(Player p : players) {
			PlayerUtil.resetPlayer(p);
		}
		Pixel.WorldProvider.removeWorld(arena.getWorld());
		taskManager.removeAllTasks();
	}
}
