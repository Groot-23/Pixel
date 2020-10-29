package me.groot_23.pixel.game.task;

import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.pixel.game.Game;

public abstract class GameTaskRepeated {
	private long tickRate;
	private BukkitRunnable runnable;
	
	boolean active = true;
	protected Game game;
	
	public GameTaskRepeated(Game game, long tickRate) {
		this.game = game;
		this.tickRate = tickRate;
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				try {	
					onUpdate();
				} catch(Exception e) {
					// Force stop on error to prevent spamming the console with an error each tick
					System.out.println("An error occurred while updating repeated Task:");
					e.printStackTrace();
					active = false;
					cancel();
				}
			}
		};
	}
	
	public void start() {
		active = true;
		onStart();
//		System.out.println("Started UI runnable!");
		runnable.runTaskTimer(game.plugin, 0, tickRate);
	}
	
	public void stop() {
		if(active) {
			active = false;
			runnable.cancel();
			onStop();
		}
	}
	
	protected void onStart() {}
	protected abstract void onUpdate();
	protected void onStop() {}
}
