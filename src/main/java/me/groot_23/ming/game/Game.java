package me.groot_23.ming.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.groot_23.ming.MiniGame;
import me.groot_23.ming.gui.GuiItem;
import me.groot_23.ming.player.GameTeam;
import me.groot_23.ming.world.Arena;

public class Game {
	
	private boolean running = false;
	
	protected GameState<?> state;
	protected JavaPlugin plugin;
	protected MiniGameMode mode;
	protected Arena arena;
	
	protected Map<ChatColor, GameTeam> teams;
	protected Inventory teamSelector;
	
	public Game(Arena arena) {
		this.mode = arena.getMode();
		this.arena = arena;
		this.state = mode.getStartingState(this);
		this.plugin = mode.getPlugin();
		this.teams = new HashMap<ChatColor, GameTeam>();
		for(ChatColor color : mode.getTeamColors()) {
			teams.put(color, new GameTeam(plugin, color));
		}
		this.teamSelector = null;
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
	
	public Collection<GameTeam> getTeams() {
		return teams.values();
	}
	
	public void createRandomTeams(List<Player> players) {
		Collections.shuffle(players);
		int i = 0;
		ChatColor[] colors = mode.getTeamColors();
		GameTeam team = null;
		for(Player player : players) {
			if(i % mode.getPlayersPerTeam() == 0) {
				team = new GameTeam(plugin, colors[i / mode.getPlayersPerTeam()]);
				teams.put(team.getColor(), team);
			}
			team.addPlayer(player);
			i++;
		}
		
		for(Player player : players) {
			for(GameTeam t : teams.values()) {
				t.addTeamToScoreboard(player.getScoreboard());
			}
		}
	}
	
	public void fillTeams(List<Player> players) {
		ChatColor[] colors = mode.getTeamColors();
		GameTeam team = null;
		int i = 0;
		for(Player player : players) {
			boolean alreadyInTeam = false;
			ChatColor color = GameTeam.getTeamOfPlayer(player, plugin);
			if(color != null) {
				GameTeam t = teams.get(color);
				if(teams != null) {
					alreadyInTeam = t.getPlayers().contains(player);
				}
			}
			System.out.println(player.getName());
			System.out.println(alreadyInTeam);
			if(!alreadyInTeam) {
				team = null;
				while(team == null && i < arena.getMaxPlayers()) {
					team = teams.get(colors[i]);
					System.out.println(team);
					System.out.println(i);
					if(team != null) {
						System.out.println(team.getPlayers().size());
						if(team.getPlayers().size() < mode.getPlayersPerTeam()) {
							team.addPlayer(player);
							break;
						} else {
							team = null;
						}
					}
					i++;
				}
			}
		}
		
		for(Player player : players) {
			for(GameTeam t : teams.values()) {
				t.addTeamToScoreboard(player.getScoreboard());
			}
		}
	}
	
	public GameTeam getTeam(ChatColor color) {
		return teams.get(color);
	}
	
	public boolean movePlayerToTeam(Player player, ChatColor team) {
		GameTeam to = teams.get(team);
		GameTeam from = teams.get(GameTeam.getTeamOfPlayer(player, mode.getPlugin()));
		if(to != null) {
			if(to.getPlayers().size() < mode.getPlayersPerTeam()) {
				to.addPlayer(player);
				if(from != null) {
					from.removePlayer(player);
				}
				return true;
			}
		}
		return false;
	}
	
	public List<GameTeam> getTeamsAlive() {
		List<GameTeam> alive = new ArrayList<GameTeam>();
		for(GameTeam team : teams.values()) {
			if(team.isAlive()) {
				alive.add(team);
			}
		}
		return alive;
	}
	
	public int getTeamsAliveCount() {
		int counter = 0;
		for(GameTeam team : teams.values()) {
			if(team.isAlive()) {
				counter++;
			}
		}
		return counter;
	}
	
	public Inventory getTeamSelectorInv() {
		if(teamSelector == null) {
			teamSelector = Bukkit.createInventory(null, 9*(mode.getPlayersPerTeam() + 1));
			ChatColor[] colors = mode.getTeamColors();
			for(int i = 0; i < arena.getMaxPlayers(); i++) {
				GuiItem guiItem = getMiniGame().createGuiItem(GameTeam.woolFromColor(colors[i]));
				guiItem.addActionClickRunnable("ming_team_selector");
				NBTItem nbt = new NBTItem(guiItem.getItem());
				nbt.setString("ming_team", colors[i].name().toLowerCase());
				
				teamSelector.setItem(i, nbt.getItem());
			}
		}
		return teamSelector;
	}
}
