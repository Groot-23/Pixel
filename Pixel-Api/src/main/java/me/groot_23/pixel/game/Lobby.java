package me.groot_23.pixel.game;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.task.PixelTaskDelayed;
import me.groot_23.pixel.game.task.PixelTaskRepeated;
import me.groot_23.pixel.player.team.TeamHandler;
import me.groot_23.pixel.world.LobbyWorld;


public abstract class Lobby {

	public final LobbyWorld world;
	public final String game;
	public final String map;
	public final JavaPlugin plugin;
	
	public final Set<Player> players = new HashSet<Player>();
	public final TeamHandler teamHandler;
	
	public final int minPlayers, maxPlayers, time; // time in ticks
	public final PixelTaskDelayed timer = new PixelTaskDelayed() {
		@Override
		public void run() {
			doTick.stop();
			doSecond.stop();
			this.cancel();
			
			Pixel.LobbyProvider.stopLobby(Lobby.this);
			
			createGame();
		}
	};
	public final PixelTaskRepeated doTick = new PixelTaskRepeated(1) {
		@Override
		protected void onUpdate() {
			onTick();
		}
	};
	public final PixelTaskRepeated doSecond = new PixelTaskRepeated(20) {
		@Override
		protected void onUpdate() {
			onSecond();
		}
	};
	
	/**
	 * Called every tick. Override it to update your GUI (Bossbar, Scoreboard, ...)
	 */
	public void onTick() {}
	
	/**
	 * Called every second. Override it if you want to execute some lobby task every second
	 */
	public void onSecond() {}
	
	public Lobby(JavaPlugin plugin, String game, String map, int minPlayers, int maxPlayers, int teamSize, int time) {
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.time = time;
		this.game = game;
		this.map = map;
		this.plugin = plugin;
		int numTeams = (maxPlayers + teamSize - 1) / teamSize;  // ceil(maxPlayers / teamSize)
		teamHandler = new TeamHandler(numTeams, teamSize);
		
		doTick.start();
		doSecond.start();
		
		world = new LobbyWorld(this, Pixel.WorldProvider.provideWorld(map), map);
	}
	
	public void join(Player player) {
		world.joinPlayer(player);
		players.add(player);
		onJoin(player);
		if(players.size() >= minPlayers) {
			if(!timer.isActive()) timer.start(time);
		}
		if(players.size() == maxPlayers) {
			timer.runTaskEarly();
		}
	}
	
	public void onLeave(Player player) {
		players.remove(player);
		if(players.size() < minPlayers) {
			timer.cancel();
		}
	}
	
	/**
	 * Create your Game
	 */
	public abstract void createGame();
	
	public void onTimerReset() {}
	
	public void onJoin(Player player) {}
}
