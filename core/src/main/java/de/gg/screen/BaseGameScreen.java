package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.google.common.eventbus.Subscribe;

import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerDisconnectedEvent;
import de.gg.event.RoundEndEvent;
import de.gg.event.ServerReadyEvent;

public abstract class BaseGameScreen extends BaseUIScreen {

	private final boolean updateGame;
	private BitmapFont font;

	// TODO dieser Screen bekommt eine Player-Hashmap, um sich um alle
	// Join/Leave und Chat-Events kümmern zu können (d.h. er kann für die
	// Netzwerk-IDs den jeweiligen Namen ermitteln)

	public BaseGameScreen(boolean updateGame) {
		this.updateGame = updateGame;
	}

	public BaseGameScreen() {
		this(true);
	}

	@Override
	protected void onInit() {
		super.onInit();

		font = skin.getFont("main-19");
	}

	@Subscribe
	public void onNewChatMessage(NewChatMessagEvent event) {
		// TODO chat messages rendern
	}

	@Subscribe
	public void onPlayerDisconnect(PlayerDisconnectedEvent event) {

	}

	@Subscribe
	public void onServerReady(ServerReadyEvent event) {
		((GameRoundendScreen) game.getScreen("roundEnd")).setServerReady();
	}

	@Subscribe
	public void onRoundEnd(RoundEndEvent event) {
		game.pushScreen("roundEnd");
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g,
				backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Background Texture
		if (backgroundTexture != null) {
			game.getSpriteBatch().begin();
			game.getSpriteBatch()
					.setProjectionMatrix(game.getUICamera().combined);
			game.getSpriteBatch().draw(this.backgroundTexture, 0, 0,
					game.getViewportWidth(), game.getViewportHeight());
			game.getSpriteBatch().end();
		}

		// The actual game
		renderGame(delta);

		// UI
		stage.getBatch().setProjectionMatrix(game.getUICamera().combined);
		stage.act(delta);
		stage.draw();

		// FPS counter
		if (game.showFPSCounter()) {
			game.getSpriteBatch().begin();
			font.draw(game.getSpriteBatch(),
					String.valueOf(Gdx.graphics.getFramesPerSecond()), 4,
					game.getViewportHeight());
			game.getSpriteBatch().end();
		}

		// Anschließend die Spiel-Logik updaten
		if (updateGame && game.getClient() != null) { // Der Client ist null,
			// wenn der Spieler gerade disconnected
			game.getClient().update();
			game.getClient().updatePing(delta);

			if (game.isHost())
				game.getServer().update();
		}
	}

	public abstract void renderGame(float delta);

}
