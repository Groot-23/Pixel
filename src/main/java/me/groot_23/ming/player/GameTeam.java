package me.groot_23.ming.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameTeam {

	// currently unused because of the char limit 16 of scoreboard names!
	private static final String SCOREBOARD_TEAM_NAME = "";

	private ChatColor color;
	private List<Player> players = new ArrayList<Player>();

	public GameTeam(ChatColor color) {
		this.color = color;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public List<Player> getPlayers() {
		return players;
	}

	public String getName() {
		return SCOREBOARD_TEAM_NAME + color.name().toLowerCase();
	}
	
	public void addPlayer(Player player) {
		players.add(player);
		updateScoreboardTeams();
	}

	public void removePlayer(Player player) {
		player.getScoreboard().getTeam(SCOREBOARD_TEAM_NAME).unregister();
		for (Player p : players) {
			if (p != player) {
				p.getScoreboard().getTeam(SCOREBOARD_TEAM_NAME).removeEntry(player.getName());
			}
		}
		players.remove(player);
	}

	public List<Player> getPlayersAlive() {
		List<Player> alive = new ArrayList<Player>();
		for (Player player : players) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				alive.add(player);
			}
		}
		return alive;
	}
	
	public int getPlayersAliveCount() {
		int counter = 0;
		for (Player player : players) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				counter++;
			}
		}
		return counter;
	}
	
	public boolean isAlive() {
		for (Player player : players) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				return true;
			}
		}
		return false;
	}

	private void updateScoreboardTeams() {
		for (Player player : players) {
			addTeamToScoreboard(player.getScoreboard());
		}
	}
	
	public void addTeamToScoreboard(Scoreboard scoreboard) {
		Team team = scoreboard.getTeam(getName());
		if (team == null) {
			team = scoreboard.registerNewTeam(getName());
			initTeamData(team);
		}
		initTeamData(team);
		for(Player player : players) {
			if (!team.getEntries().contains(player.getName())) {
				team.addEntry(player.getName());
			}
		}
	}

	private void initTeamData(Team team) {
		team.setAllowFriendlyFire(false);
		team.setPrefix(color + "[" + color.name() + "] ");
	}

}
