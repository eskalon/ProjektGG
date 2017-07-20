package dev.gg.screen;

/**
 * This screen is rendered, when the player is inside of a house.
 */
public class GameHouseScreen extends BaseScreen {

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		game.getCurrentSession().update(delta);

		game.getCurrentSession().renderHouse(delta);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
