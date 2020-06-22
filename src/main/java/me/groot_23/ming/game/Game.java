package me.groot_23.ming.game;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.world.Arena;

public class Game {
	
	private GameState<?> state;
	private JavaPlugin plugin;
	private MiniGame game;
	private World w;
	
	public Game(MiniGame game, Arena arena) {
		this.game = game;
		w = arena.getWorld();
		this.state = game.getStartingState(arena);
		this.plugin = game.getPlugin();
	}
	
	public void startGame() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(state != null) {
					state = state.update();
				} else {
					game.getArenaProvider().stopArena(w);
					cancel();
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}
}
