package me.groot_23.pixel.game.task;

import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.pixel.Pixel;

public abstract class PixelTaskDelayed {
	
	private long start;
	private long end;
	private BukkitRunnable runnable;
	private long delay;
	
	
	public PixelTaskDelayed() {
		runnable = null;
	}
	
	public long getRemainingTicks() {
		return end - Pixel.getTime();
	}
	public long getCurrentTicks() {
		return Pixel.getTime() - start;
	}
	public int getRemainingSeconds() {
		return Math.max((int) (getRemainingTicks() / 20), 0 );
	}
	public int getCurrentSeconds() {
		return (int) (getCurrentTicks() / 20);
	}
	public double getRemainingProgress() {
		return Math.max((double) getRemainingTicks() / delay, 0);
	}
	public double getCurrentProgress() {
		return (double) getCurrentTicks() / delay;
	}
	
	public void start(long delay) {
		// create new runnable as it's not allowed to rerun the same instance
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				PixelTaskDelayed.this.run();
			}
		};
		
		this.delay = delay;
		start = Pixel.getTime();
		end = start + delay;
		runnable.runTaskLater(Pixel.getPlugin(), delay);
	}
	
	public void cancel() {
		if(isActive()) {
			runnable.cancel();
			runnable = null;
		}
	}
	
	public void restart(long delay) {
		this.delay = delay;
		cancel();
		start(delay);
	}
	
	public void runTaskEarly() {
		if(isActive()) {
			cancel();
			run();
		}
	}
	
	public long getDelay() {
		return delay;
	}
	
	public boolean isActive() {
		return runnable != null;
	}
	
	public abstract void run();
}
