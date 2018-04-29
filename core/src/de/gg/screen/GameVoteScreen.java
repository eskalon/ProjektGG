package de.gg.screen;

public class GameVoteScreen extends BaseGameScreen {

	public GameVoteScreen() {
		super(false);
	}

	@Override
	public void renderGame(float delta) {
		game.getClient().updatePing(delta);

		// if (game.isHost())
		// game.getServer().update();
	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub

	}

}
