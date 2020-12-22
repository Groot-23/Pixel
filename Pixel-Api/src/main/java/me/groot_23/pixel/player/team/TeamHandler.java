package me.groot_23.pixel.player.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.groot_23.pixel.gui.GuiItem;
import me.groot_23.pixel.gui.runnables.TeamSelectorRunnable;

public class TeamHandler {

	public DyeColor[] colors;
	protected Map<DyeColor, GameTeam> teams = new HashMap<DyeColor, GameTeam>();
	protected Inventory teamSelector;
	public final int numTeams;
	public final int teamSize;
	
	public TeamHandler(int numTeams, int teamSize) {
		this(numTeams, teamSize, new DyeColor[] {DyeColor.GREEN, DyeColor.YELLOW, DyeColor.RED, DyeColor.BLUE, DyeColor.ORANGE,
				DyeColor.LIGHT_BLUE, DyeColor.LIME, DyeColor.PURPLE, DyeColor.CYAN, DyeColor.MAGENTA});
	}
	
	public TeamHandler(int numTeams, int teamSize, DyeColor[] colors) {
		this.colors = colors;
		for(DyeColor color : colors) {
			teams.put(color, new GameTeam(color));
		}
		this.teamSelector = null;
		this.numTeams = numTeams;
		this.teamSize = teamSize;
	}
	
	public Collection<GameTeam> getTeams() {
		return teams.values();
	}
	
	public void createRandomTeams(List<Player> players) {
		Collections.shuffle(players);
		int i = 0;
		GameTeam team = null;
		for(Player player : players) {
			if(i % teamSize == 0) {
				team = new GameTeam(colors[i / teamSize]);
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
		GameTeam team = null;
		int i = 0;
		for(Player player : players) {
			boolean alreadyInTeam = false;
			DyeColor color = GameTeam.getTeamOfPlayer(player);
			if(color != null) {
				GameTeam t = teams.get(color);
				if(teams != null) {
					alreadyInTeam = t.getPlayers().contains(player);
				}
			}
			if(!alreadyInTeam) {
				team = null;
				while(team == null && i < numTeams) {
					team = teams.get(colors[i]);
					if(team != null) {
						if(team.getPlayers().size() < teamSize) {
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
	
	public GameTeam getTeam(DyeColor color) {
		return teams.get(color);
	}
	
	public boolean movePlayerToTeam(Player player, DyeColor team) {
		GameTeam to = teams.get(team);
		GameTeam from = teams.get(GameTeam.getTeamOfPlayer(player));
		if(to != null) {
			if(to.getPlayers().size() < teamSize) {
				to.addPlayer(player);
				if(from != null) {
					from.removePlayer(player);
				}
				return true;
			}
		}
		return false;
	}
	
	public void removePlayer(Player player) {
		DyeColor col = GameTeam.getTeamOfPlayer(player);
		GameTeam gt = teams.get(col);
		if(gt != null) {
			gt.removePlayer(player);
		}
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
			teamSelector = Bukkit.createInventory(null, 9*(teamSize + 1));
			for(int i = 0; i < numTeams; i++) {
				Material wool = GameTeam.toWool(colors[i]);
				if(wool != null) {
					GuiItem guiItem = new GuiItem(wool);
					guiItem.addClickRunnable(new TeamSelectorRunnable(colors[i]));
					teamSelector.setItem(i, guiItem.getItem());
				}

			}
		}
		return teamSelector;
	}
}
