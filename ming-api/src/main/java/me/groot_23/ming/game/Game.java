package me.groot_23.ming.game;

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

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.game.task.GameTaskManager;
import me.groot_23.ming.player.team.TeamHandler;
import me.groot_23.ming.world.Arena;

public abstract class Game {
	
	
	public final MiniGame miniGame;
	public final MiniGameMode mode;
	public final Arena arena;
	public final JavaPlugin plugin;
	
	public final GameTaskManager taskManager;
	
	public final TeamHandler teamHandler;
	
	public final int id;
	private static int currentID = 0;

	
	public Game(MiniGameMode mode, String worldGroup) {
		this.id = currentID++;
		this.mode = mode;
		this.miniGame = mode.miniGame;
		this.plugin = mode.plugin;
		this.allowJoin = true;
		arena = miniGame.worldProvider.provideArena(this, miniGame.worldProvider.getWorldGroup(worldGroup), 100);
		teamHandler = new TeamHandler(mode, arena.getMaxPlayers());
		taskManager = new GameTaskManager();
	}
	
	public Game(MiniGameMode mode, String[] possibleMaps) {
		this.id = currentID++;
		this.mode = mode;
		this.miniGame = mode.miniGame;
		this.allowJoin = true;
		this.plugin = mode.plugin;
		arena = miniGame.worldProvider.provideArena(this, possibleMaps, 100);
		teamHandler = new TeamHandler(mode, arena.getMaxPlayers());
		taskManager = new GameTaskManager();
	}
	
	public Arena createArena(World world, String map) {
		return new Arena(this, world, map);
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
		mode.gameProvider.stopJoin(this);
	}
	
	public void endGame() {
		mode.gameProvider.stopGame(this);
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
		miniGame.worldProvider.removeWorld(arena.getWorld());
		taskManager.removeAllTasks();
	}
}
