package de.gg.screen;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import de.gg.input.DefaultInputProcessor;
import de.gg.util.Log;

/**
 * This screen is rendered, when the player is inside of a house.
 */
public class GameInHouseScreen extends BaseGameScreen {

	private short selectedHouseId;

	@Override
	protected void onInit() {
		super.onInit();
		// TODO Auto-generated method stub
	}

	@Override
	protected void initUI() {
		// TODO Auto-generated method stub
	}

	@Override
	public void renderGame(float delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		super.show();
		game.getInputMultiplexer().addProcessor(new DefaultInputProcessor() {

			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.ESCAPE) {
					game.pushScreen("map");
					return true;
				}
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {
				if (button == Buttons.RIGHT) {
					game.pushScreen("map");
					return true;
				}
				return false;
			}
		});

		Log.debug("Input", "Double selection: %d", selectedHouseId);
	}

	public void setSelectedHouseId(short selectedHouseId) {
		this.selectedHouseId = selectedHouseId;
	}

	@Override
	public void dispose() {
		super.dispose();

		// if (isLoaded())
		// dispose loaded stuff
	}

}
