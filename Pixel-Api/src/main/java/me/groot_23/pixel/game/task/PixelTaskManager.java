package me.groot_23.pixel.game.task;

import java.util.HashMap;
import java.util.Map;

public class PixelTaskManager {

	private Map<String, PixelTaskDelayed> tasks = new HashMap<String, PixelTaskDelayed>();
	private Map<String, PixelTaskRepeated> uis = new HashMap<String, PixelTaskRepeated>();
	
	public void addTask(PixelTaskDelayed task, long delay, String name) {
		PixelTaskDelayed old = tasks.get(name);
		if(old != null) {
			old.cancel();
		}
		tasks.put(name, task);
		task.start(delay);
	}
	public PixelTaskDelayed getTask(String name) {
		return tasks.get(name);
	}
	public void removeTask(String name) {
		PixelTaskDelayed task = tasks.get(name);
		if(task != null) {
			task.cancel();
		}
		tasks.remove(name);
	}
	
	public void addRepeated(PixelTaskRepeated task, String name) {
		PixelTaskRepeated old = uis.get(name);
		if(old != null) {
			old.stop();
		}
		uis.put(name, task);
		task.start();
	}
	public PixelTaskRepeated getRepeated(String name) {
		return uis.get(name);
	}
	public void removeRepeated(String name) {
		PixelTaskRepeated task = uis.get(name);
		if(task != null) {
			task.stop();
		}
		uis.remove(name);
	}
	
	public void removeAllTasks() {
		for(PixelTaskDelayed task : tasks.values()) {
			task.cancel();
		}
		tasks.clear();
		for(PixelTaskRepeated task : uis.values()) {
			task.stop();
		}
		uis.clear();
	}
}
