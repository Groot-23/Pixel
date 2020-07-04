package me.groot_23.ming.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameTeam {

	// currently unused because of the char limit 16 of scoreboard names!
	private static final String SCOREBOARD_TEAM_NAME = "";

	private JavaPlugin plugin;
	private ChatColor color;
	private List<Player> players = new ArrayList<Player>();

	public GameTeam(JavaPlugin plugin, ChatColor color) {
		this.plugin = plugin;
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
		System.out.println(color.name().toLowerCase());
		player.setMetadata("ming_team", new FixedMetadataValue(plugin, color.name().toLowerCase()));
	}

	public void removePlayer(Player player) {
		for (Player p : players) {
			Team team = p.getScoreboard().getTeam(SCOREBOARD_TEAM_NAME);
			if(team != null) {
				team.removeEntry(player.getName());
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
	
	public static ChatColor getTeamOfPlayer(Player player, JavaPlugin plugin) {
		List<MetadataValue> values = player.getMetadata("ming_team");
		if(values != null) {
			for(MetadataValue val : values) {
				if(val.getOwningPlugin() == plugin) {
					System.out.println(val.asString());
					return ChatColor.valueOf(val.asString().toUpperCase());
				}
			}
		}
		return null;
	}
	
	public static Material woolFromColor(ChatColor color) {
		switch(color) {
		case BLUE: return Material.BLUE_WOOL;
		case RED: return Material.RED_WOOL;
		case YELLOW: return Material.YELLOW_WOOL;
		case GREEN: return Material.GREEN_WOOL;
		case WHITE: return Material.WHITE_WOOL;
		case AQUA: return Material.LIGHT_BLUE_WOOL;
		case DARK_PURPLE: return Material.PURPLE_WOOL;
		case LIGHT_PURPLE: return Material.MAGENTA_WOOL;
		case DARK_GRAY: return Material.GRAY_WOOL;
		case GRAY: return Material.LIGHT_GRAY_WOOL;
		case DARK_AQUA: return Material.CYAN_WOOL;
		case BLACK: return Material.BLACK_WOOL;
		
		default: return null;
		}
	}

}
