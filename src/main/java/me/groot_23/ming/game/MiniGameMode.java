package me.groot_23.ming.game;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.provider.GameProvider;

public abstract class MiniGameMode {
	
	protected MiniGame miniGame;
//	protected ArenaProvider arenaProvider;
	protected JavaPlugin plugin;
//	protected WorldProvider worldProvider;
	public final GameProvider gameProvider;
	
	public MiniGameMode(MiniGame miniGame) {
		this.plugin = miniGame.getPlugin();
		this.miniGame = miniGame;
		this.gameProvider = new GameProvider(this);
		
//		arenaProvider = new ArenaProvider(this);
	}
	
	public JavaPlugin getPlugin() {
		return miniGame.getPlugin();
	}
	public MiniGame getMiniGame() {
		return miniGame;
	}
//	public ArenaProvider getArenaProvider() {
//		return arenaProvider;
//	}
	
	public abstract String getName();
	
//	public Arena createArena(World world, String map) {
//		return new Arena(this, world, map);
//	}
	
	
	public int getPlayersPerTeam() {
		return 1;
	}
	
	public abstract Game createNewGame();
	
	
	/**
	 * Specify the order of team colors. If you have 4 teams then only the first four colors will be used
	 * If you would use ChatColor.values() you would get black as your first color which you probably don't want!
	 */
	public ChatColor[] getTeamColors() {
		return new ChatColor[] {ChatColor.GREEN, ChatColor.YELLOW, ChatColor.BLUE, ChatColor.RED, ChatColor.AQUA, ChatColor.DARK_GREEN,
				ChatColor.GOLD, ChatColor.GRAY, ChatColor.LIGHT_PURPLE, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.DARK_RED};
	}
	
}
