package me.groot_23.ming.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.player.GameTeam;
import me.groot_23.ming.world.Arena;

public class Game {
	
	private boolean running = false;
	
	protected GameState<?> state;
	protected JavaPlugin plugin;
	protected MiniGameMode mode;
	protected Arena arena;
	
	protected List<GameTeam> teams;
	
	public Game(Arena arena) {
		this.mode = arena.getMode();
		this.arena = arena;
		this.state = mode.getStartingState(this);
		this.plugin = mode.getPlugin();
	}
	
	public void startGame() {
		if(running) throw new IllegalStateException("You can't start a game while it is running!");
		running = true;
		state.onStart();
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(state != null) {
					state = state.update();
				} else {
					mode.getArenaProvider().stopArena(arena.getWorld());
					cancel();
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	public GameState<?> getState() {
		return state;
	}
	public Arena getArena() {
		return arena;
	}
	public MiniGameMode getMode() {
		return mode;
	}
	public MiniGame getMiniGame() {
		return mode.getMiniGame();
	}
	
	public List<GameTeam> getTeams() {
		return teams;
	}
	
	public void createRandomTeams(List<Player> players) {
		Collections.shuffle(players);
		teams = new ArrayList<GameTeam>();
		int i = 0;
		ChatColor[] colors = mode.getTeamColors();
		GameTeam team = null;
		for(Player player : players) {
			if(i % mode.getPlayersPerTeam() == 0) {
				team = new GameTeam(colors[i / mode.getPlayersPerTeam()]);
				teams.add(team);
			}
			team.addPlayer(player);
			i++;
		}
		
		for(Player player : players) {
			for(GameTeam t : teams) {
				t.addTeamToScoreboard(player.getScoreboard());
			}
		}
	}
	
	public List<GameTeam> getTeamsAlive() {
		List<GameTeam> alive = new ArrayList<GameTeam>();
		for(GameTeam team : teams) {
			if(team.isAlive()) {
				alive.add(team);
			}
		}
		return alive;
	}
	
	public int getTeamsAliveCount() {
		int counter = 0;
		for(GameTeam team : teams) {
			if(team.isAlive()) {
				counter++;
			}
		}
		return counter;
	}
}
