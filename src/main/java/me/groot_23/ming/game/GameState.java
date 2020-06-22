package me.groot_23.ming.game;

public abstract class GameState<Data extends GameData> {
	
	protected Data data;
	
	public GameState(Data data) {
		this.data = data;
	}
	
	public GameState(GameState<Data> state) {
		this.data = state.data;
	}
	
	public final GameState<Data> update() {
		GameState<Data> nextState = onUpdate();
		if(nextState != this && nextState != null) {
			onEnd();
			nextState.onStart();
		}
		return nextState;
	}
	
	protected void onStart() {}
	protected abstract GameState<Data> onUpdate();
	protected void onEnd() {}
}
