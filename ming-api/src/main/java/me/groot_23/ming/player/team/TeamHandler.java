package me.groot_23.ming.player.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import de.tr7zw.nbtapi.NBTItem;
import me.groot_23.ming.game.MiniGameMode;
import me.groot_23.ming.gui.GuiItem;

public class TeamHandler {

	protected Map<ChatColor, GameTeam> teams;
	protected Inventory teamSelector;
	protected MiniGameMode mode;
	protected JavaPlugin plugin;
	protected int maxPlayers;
	
	public TeamHandler(MiniGameMode mode, int maxPlayers) {
		this.mode = mode;
		this.plugin = mode.getPlugin();
		this.teams = new HashMap<ChatColor, GameTeam>();
		for(ChatColor color : mode.getTeamColors()) {
			teams.put(color, new GameTeam(plugin, color));
		}
		this.teamSelector = null;
		this.maxPlayers = maxPlayers;
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
			if(!alreadyInTeam) {
				team = null;
				while(team == null && i < maxPlayers) {
					team = teams.get(colors[i]);
					if(team != null) {
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
			for(int i = 0; i < maxPlayers; i++) {
				Material wool = GameTeam.woolFromColor(colors[i]);
				if(wool != null) {
					GuiItem guiItem = new GuiItem(wool);
					guiItem.addActionClickRunnable("ming_team_selector");
					NBTItem nbt = new NBTItem(guiItem.getItem());
					nbt.setString("ming_team", colors[i].name().toLowerCase());
					
					teamSelector.setItem(i, nbt.getItem());
				}

			}
		}
		return teamSelector;
	}
}
