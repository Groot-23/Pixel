package me.groot_23.ming.game.task;

import org.bukkit.scheduler.BukkitRunnable;

import me.groot_23.ming.MinG;
import me.groot_23.ming.game.Game;

public abstract class GameTaskDelayed {
	
	private long start;
	private long end;
	private BukkitRunnable runnable;
	private boolean active;
	
	protected Game game;
	protected long delay;
	
	public GameTaskDelayed(Game game, long delay) {
		this.game = game;
		this.delay = delay;
		this.active = false;
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				GameTaskDelayed.this.run();
				active = false;
			}
		};
	}
	
	public long getRemainingTicks() {
		return end - MinG.getTime();
	}
	public long getCurrentTicks() {
		return MinG.getTime() - start;
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
		active = true;
		start = MinG.getTime();
		end = start + delay;
		runnable.runTaskLater(game.miniGame.getPlugin(), delay);
	}
	public void start() {
		start(delay);
	}
	
	public void cancel() {
		if(active) {
			active = false;
			runnable.cancel();
		}
	}
	
	public void restart(long delay) {
		cancel();
		start(delay);
	}
	public void restart() {
		restart(delay);
	}
	
	public void runTaskEarly() {
		if(active) {
			cancel();
			run();
		}
	}
	
	public long getDelay() {
		return delay;
	}
	
	public abstract void run();
}
