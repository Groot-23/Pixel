package me.groot_23.ming.provider;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import me.groot_23.ming.game.Game;
import me.groot_23.ming.game.MiniGameMode;

public class GameProvider {
	private MiniGameMode mode;
	private Map<Integer, Game> gameById = new HashMap<Integer, Game>();
	private Game currentGame;
	
	public GameProvider(MiniGameMode mode) {
		this.mode = mode;
	}
	
	public Game getGame(int id) {
		return gameById.get(id);
	}
	
	public Game provideGame() {
		if(currentGame == null) {
			currentGame = mode.createNewGame();
			if(currentGame != null) {
				gameById.put(currentGame.id, currentGame);
			}
		}
		return currentGame;
	}
	
	public void joinPlayer(Player player) {
		Game game = provideGame();
		if(game != null) {
			game.joinPlayer(player);
		}
	}
	
	public void stopJoin(Game game) {
		if(game == currentGame) {
			currentGame = null;
		}
	}
	
	public void stopGame(Game game) {
		if(game == currentGame) {
			currentGame = null;
		}
		game.onEnd();
		gameById.remove(game.id);
	}
	
}
