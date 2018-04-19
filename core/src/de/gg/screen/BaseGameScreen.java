package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.google.common.eventbus.Subscribe;

import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerDisconnectedEvent;
import de.gg.event.RoundEndEvent;

public abstract class BaseGameScreen extends BaseUIScreen {

	private final boolean updateGame;

	// TODO dieser Screen bekommt eine Player-Hashmap, um sich um alle
	// Join/Leave und Chat-Events kümmern zu können (d.h. er kann für die
	// Netzwerk-IDs den jeweiligen Namen ermitteln)

	public BaseGameScreen(boolean updateGame) {
		this.updateGame = updateGame;
	}

	public BaseGameScreen() {
		this(true);
	}

	@Subscribe
	public void onNewChatMessage(NewChatMessagEvent event) {
		// TODO
	}

	@Subscribe
	public void onPlayerDisconnect(PlayerDisconnectedEvent event) {

	}

	@Subscribe
	public void onRoundEndDataArrived(RoundEndEvent event) {
		((GameRoundendScreen) game.getScreen("roundEnd"))
				.setData(event.getData());
	}

	@Override
	public void render(float delta) {
		if (updateGame && game.getCurrentSession() != null) {
			// Die Session kann null sein, wenn der Client gerade disconnected
			if (game.getCurrentSession().update()) {
				game.pushScreen("roundEnd");
			}

			game.getNetworkHandler().updatePing(delta);
			game.getNetworkHandler().updateServer();
		}

		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (backgroundTexture != null) {
			game.getSpriteBatch().begin();
			game.getSpriteBatch()
					.setProjectionMatrix(game.getUICamera().combined);
			game.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
					game.getViewportWidth(), game.getViewportHeight());
			game.getSpriteBatch().end();
		}

		renderGame(delta);

		stage.getBatch().setProjectionMatrix(game.getUICamera().combined);
		stage.act(delta);
		stage.draw();
	}

	public abstract void renderGame(float delta);

}
