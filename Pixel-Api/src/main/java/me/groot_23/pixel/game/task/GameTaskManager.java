package me.groot_23.pixel.game.task;

import java.util.HashMap;
import java.util.Map;

public class GameTaskManager {

	private Map<String, GameTaskDelayed> tasks = new HashMap<String, GameTaskDelayed>();
	private Map<String, GameTaskRepeated> uis = new HashMap<String, GameTaskRepeated>();
	
	public void addTask(GameTaskDelayed task, String name) {
		GameTaskDelayed old = tasks.get(name);
		if(old != null) {
			old.cancel();
		}
		tasks.put(name, task);
		task.start();
	}
	public GameTaskDelayed getTask(String name) {
		return tasks.get(name);
	}
	public void removeTask(String name) {
		GameTaskDelayed task = tasks.get(name);
		if(task != null) {
			task.cancel();
		}
		tasks.remove(name);
	}
	
	public void addRepeated(GameTaskRepeated task, String name) {
		GameTaskRepeated old = uis.get(name);
		if(old != null) {
			old.stop();
		}
		uis.put(name, task);
		task.start();
	}
	public GameTaskRepeated getRepeated(String name) {
		return uis.get(name);
	}
	public void removeRepeated(String name) {
		GameTaskRepeated task = uis.get(name);
		if(task != null) {
			task.stop();
		}
		uis.remove(name);
	}
	
	public void removeAllTasks() {
		for(GameTaskDelayed task : tasks.values()) {
			task.cancel();
		}
		tasks.clear();
		for(GameTaskRepeated task : uis.values()) {
			task.stop();
		}
		uis.clear();
	}
}
