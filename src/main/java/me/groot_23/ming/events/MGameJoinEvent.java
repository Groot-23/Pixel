package me.groot_23.ming.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MGameJoinEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public MGameJoinEvent(Player player) {
		this.player = player;
	}
	
	private Player player;
	public Player getPlayer() {
		return player;
	}
	
}
