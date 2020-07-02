package me.groot_23.ming.game;

import java.util.ArrayList;
import java.util.List;

import me.groot_23.ming.MiniGame;
import me.groot_23.ming.player.GameTeam;

public abstract class GameState<Data extends GameData> {
	
	protected Data data;
	protected MiniGame miniGame;
	protected MiniGameMode gameMode;
	protected Game game;
	
	public GameState(Data data, Game game) {
		this.data = data;
		this.game = game;
		this.gameMode = game.getMode();
		this.miniGame = game.getMiniGame();
	}
	
	public GameState(GameState<Data> state) {
		this.data = state.data;
		this.miniGame = state.miniGame;
		this.gameMode = state.gameMode;
		this.game = state.game;
	}
	
	public final GameState<Data> update() {
		GameState<Data> nextState = onUpdate();
		if(nextState != this && nextState != null) {
			onEnd();
			nextState.onStart();
		}
		return nextState;
	}
	
	public void onStart() {}
	protected abstract GameState<Data> onUpdate();
	protected void onEnd() {}
}
