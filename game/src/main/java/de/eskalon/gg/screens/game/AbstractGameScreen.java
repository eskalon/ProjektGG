package de.eskalon.gg.screens.game;

import com.badlogic.gdx.Gdx;

import de.eskalon.commons.event.Subscribe;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.screens.AbstractEskalonUIScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.events.ChatMessageEvent;
import de.eskalon.gg.events.ConnectionLostEvent;
import de.eskalon.gg.events.LobbyDataChangedEvent;
import de.eskalon.gg.screens.ServerBrowserScreen;

public abstract class AbstractGameScreen extends AbstractEskalonUIScreen {

	protected @Inject ProjektGGApplicationContext appContext;
	protected @Inject EskalonScreenManager screenManager;

	private final boolean updateSession;

	public AbstractGameScreen(boolean updateSession) {
		this.updateSession = updateSession;
	}

	public AbstractGameScreen() {
		this(true);
	}

	@Override
	protected void renderBackground(float delta) {
		// Background image
		super.renderBackground(delta);

		// Actual 3d game content
		renderGame(delta);
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		updateGame(delta);
	}

	public void updateGame(float delta) {
		if (appContext.getClient() == null)
			return;

		if (updateSession) {
			if (appContext.getGameHandler().update(delta))
				screenManager.pushScreen(RoundEndScreen.class, "circle_crop");
		}

		appContext.getClient().updatePing(Gdx.graphics.getDeltaTime());
	}

	@Subscribe
	public void onLobbyDataChangedEvent(LobbyDataChangedEvent ev) {
		// TODO do stuff; e.g. remove disconnected player from world
	}

	@Subscribe
	public void onChatMessageEvent(ChatMessageEvent<?> event) {
		// TODO do stuff
	}

	@Subscribe
	public void onConnectionLost(ConnectionLostEvent ev) {
		if (this == screenManager.getCurrentScreen()) { // If this screen is
														// rendered as part of a
														// transition, the next
														// screen is responsible
														// for handling the
														// lost connection
			appContext.handleDisconnection();

			ServerBrowserScreen screen = EskalonInjector.instance()
					.getInstance(ServerBrowserScreen.class);
			screen.setJustDisconnectedFromServer(true);
			screenManager.pushScreen(screen, null);
		}
	}

	public abstract void renderGame(float delta);

}
