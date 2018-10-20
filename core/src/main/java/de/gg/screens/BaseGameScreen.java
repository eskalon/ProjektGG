package de.gg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.google.common.eventbus.Subscribe;

import de.gg.events.DisconnectionEvent;
import de.gg.events.NewChatMessagEvent;
import de.gg.events.PlayerDisconnectedEvent;
import de.gg.events.RoundEndEvent;
import de.gg.events.ServerReadyEvent;
import de.gg.network.GameServer;
import de.gg.utils.SimpleListener;
import de.gg.utils.log.Log;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

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
	protected void onInit(AnnotationAssetManager assetManager) {
		super.onInit(assetManager);

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
	public void onDisconnection(DisconnectionEvent event) {
		if (game.getClient() != null) { // unexpected disconnection
			Log.info("Client", "Verbindung zum Server getrennt");

			game.setClient(null);
			final GameServer server = game.getServer();
			game.setServer(null);

			// Close server
			(new Thread(() -> {
				if (server != null) {
					server.stop();

					Log.info("Server", "Server beendet");
				}
			})).start();

			showInfoDialog("Fehler", "Verbindung zum Server getrennt", true,
					new SimpleListener() {
						@Override
						public void listen(Object param) {
							game.pushScreen("mainMenu");
						}
					});
		}
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
