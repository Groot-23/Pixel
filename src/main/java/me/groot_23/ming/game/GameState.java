package me.groot_23.ming.game;

import me.groot_23.ming.MiniGame;

public abstract class GameState<Data extends GameData> {
	
	protected Data data;
	protected MiniGame game;
	
	public GameState(Data data, MiniGame game) {
		this.data = data;
		this.game = game;
	}
	
	public GameState(GameState<Data> state) {
		this.data = state.data;
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
