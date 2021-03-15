package me.groot_23.pixel.game.task;

import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.pixel.Pixel;

public abstract class PixelTaskRepeated {
	private long tickRate;
	private BukkitRunnable runnable;
	
	public PixelTaskRepeated(long tickRate) {
		this.tickRate = tickRate;
		runnable = null;
	}
	
	public void start() {
		// create new runnable as it's not allowed to rerun the same instance
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				try {	
					onUpdate();
				} catch(Exception e) {
					// Force stop on error to prevent spamming the console with an error each tick
					System.out.println("An error occurred while updating repeated Task:");
					e.printStackTrace();
					cancel();
				}
			}
		};
		onStart();
		runnable.runTaskTimer(Pixel.getPlugin(), 0, tickRate);
	}
	
	public void stop() {
		if(isActive()) {
			runnable.cancel();
			runnable = null;
			onStop();
		}
	}
	
	public boolean isActive() {
		return runnable != null;
	}
	
	protected void onStart() {}
	protected abstract void onUpdate();
	protected void onStop() {}
}
