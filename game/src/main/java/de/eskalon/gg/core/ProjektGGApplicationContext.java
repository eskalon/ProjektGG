package de.eskalon.gg.core;

import javax.annotation.Nullable;

import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.core.EskalonApplicationContext;
import de.eskalon.gg.graphics.rendering.GameRenderer;
import de.eskalon.gg.net.GameClient;
import de.eskalon.gg.net.GameServer;
import de.eskalon.gg.simulation.GameHandler;
import de.eskalon.gg.thirdparty.DiscordGGHandler;

public class ProjektGGApplicationContext extends EskalonApplicationContext {

	private static final Logger LOG = LoggerService
			.getLogger(ProjektGGApplicationContext.class);

	private @Nullable GameRenderer gameRenderer;
	private @Nullable GameHandler gameHandler;
	private @Nullable GameServer server;
	private @Nullable GameClient client;

	public ProjektGGApplicationContext(EskalonApplicationContext appContext) {
		super(appContext.getAppName(), appContext.getVersion(),
				appContext.getTransitions());
	}

	public boolean isInGame() {
		return client != null;
	}

	/**
	 * @return the game client; {@code null} if the player is not in a game or
	 *         currently disconnecting from one
	 */
	public @Nullable GameClient getClient() {
		return client;
	}

	public void setClient(@Nullable GameClient client) {
		this.client = client;
	}

	/**
	 * @return the game server; {@code null} if the player is not hosting a game
	 */
	public @Nullable GameServer getServer() {
		return server;
	}

	public void setServer(@Nullable GameServer server) {
		this.server = server;
	}

	public boolean isHost() {
		return server != null;
	}

	public GameHandler getGameHandler() {
		return gameHandler;
	}

	public void setGameHandler(GameHandler gameHandler) {
		this.gameHandler = gameHandler;
	}

	public GameRenderer getGameRenderer() {
		return gameRenderer;
	}

	public void setGameRenderer(GameRenderer gameRenderer) {
		this.gameRenderer = gameRenderer;
	}

	public void handleDisconnection() {
		if (gameHandler != null)
			gameHandler.dispose();
		gameHandler = null;

		if (gameRenderer != null)
			gameRenderer.dispose();
		gameRenderer = null;

		final GameClient client = this.client;
		this.client = null; // set to null to stop updates

		final GameServer server = this.server;
		this.server = null;

		ThreadHandler.getInstance().executeRunnable(() -> {
			client.stop();

			if (server != null) {
				server.stop();
			}
		});

		DiscordGGHandler.instance().setMenuPresence();
	}

	@Override
	public String toString() {
		return "ProjektGGApplicationContext{appName=" + getAppName()
				+ ",version=" + getVersion() + ",transitions="
				+ getTransitions() + ",gameHandler=" + gameHandler
				+ ",gameRenderer=" + gameRenderer + "}";
	}

}
