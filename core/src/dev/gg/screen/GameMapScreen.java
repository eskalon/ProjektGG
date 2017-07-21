package dev.gg.screen;

import dev.gg.network.Player;
import dev.gg.network.event.ClientEventHandler;

/**
 * This screen is the main game screen and is rendered when the player is in the
 * city map.
 */
public class GameMapScreen extends BaseScreen implements ClientEventHandler {

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		game.getCurrentSession().update();

		game.getCurrentSession().renderMap(delta, 0, 0);
	}

	@Override
	public void show() {
		// game.setInputProcessor(this);
		game.getCurrentMultiplayerSession().setClientEventHandler(this);
	}

	@Override
	public void hide() {
		// game.setInputProcessor(null);
		game.getCurrentMultiplayerSession().setClientEventHandler(null);
	}

	@Override
	public void onNewChatMessage(short senderId, String message) {
		// TODO
	}

	@Override
	public void onPlayerDisconnect(Player player) {
		// TODO
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
