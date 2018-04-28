package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.google.common.eventbus.Subscribe;

import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerDisconnectedEvent;
import de.gg.event.RoundEndDataReceivedEvent;
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
	public void onRoundEndDataArrived(RoundEndDataReceivedEvent event) {
		((GameRoundendScreen) game.getScreen("roundEnd"))
				.setData(event.getData());
	}

	@Subscribe
	public void onRoundEnd(RoundEndEvent event) {
		game.pushScreen("roundEnd");
	}

	@Override
	public void render(float delta) {
		if (updateGame && game.getClient() != null) { // Der Client kann null
														// sein, wenn der
														// Spieler gerade
														// disconnected
			game.getClient().update();
			game.getClient().updatePing(delta);

			if (game.isHost())
				game.getServer().update();
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
