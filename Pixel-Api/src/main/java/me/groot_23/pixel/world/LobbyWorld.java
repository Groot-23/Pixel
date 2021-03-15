package me.groot_23.pixel.world;

import org.bukkit.World;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.Lobby;

public class LobbyWorld extends PixelWorld {

	public final Lobby lobby;
	
	public LobbyWorld(Lobby lobby, World world, String mapName) {
		super(lobby.plugin, world, mapName);
		this.lobby = lobby;
		Pixel.lobbyWorlds.put(world.getUID(), this);
	}
	
	public LobbyWorld(Lobby lobby, PixelWorld pworld) {
		super(pworld);
		this.lobby = lobby;
		Pixel.lobbyWorlds.put(world.getUID(), this);
	}

}
