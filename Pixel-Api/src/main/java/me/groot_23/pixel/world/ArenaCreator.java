package me.groot_23.pixel.world;

import org.bukkit.World;

import me.groot_23.pixel.game.Game;

public interface ArenaCreator {
	Arena createArena(Game game, World world, String map);
	
	public static class Default implements ArenaCreator {
		@Override
		public Arena createArena(Game game, World world, String map) {
			return new Arena(game, world, map);
		}
	}
}
