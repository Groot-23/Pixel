package me.groot_23.pixel.world;

import org.bukkit.World;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.game.Game;

public class GameWorld extends PixelWorld{

	public final Game game;
	
	public GameWorld(Game game, World world, String mapName) {
		super(game.plugin, world, mapName);
		this.game = game;
		Pixel.gameWorlds.put(world.getUID(), this);
	}
	
	public GameWorld(Game game, PixelWorld pworld) {
		super(pworld);
		this.game = game;
		Pixel.gameWorlds.put(world.getUID(), this);
	}
}
