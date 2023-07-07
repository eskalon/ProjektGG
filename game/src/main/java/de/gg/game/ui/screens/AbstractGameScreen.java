package de.gg.game.ui.screens;

import com.badlogic.gdx.Gdx;

import de.damios.guacamole.concurrent.ThreadHandler;
import de.eskalon.commons.event.Subscribe;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.ConnectionLostEvent;
import de.gg.game.events.RoundEndEvent;
import de.gg.game.events.ServerReadyEvent;
import de.gg.game.events.UIRefreshEvent;
import de.gg.game.network.GameServer;

public abstract class AbstractGameScreen extends AbstractGGUIScreen {

	private final boolean updateSession;
	private boolean dirty = false;

	public AbstractGameScreen(ProjektGGApplication application,
			boolean updateSession) {
		super(application);
		this.updateSession = updateSession;
	}

	public AbstractGameScreen(ProjektGGApplication app) {
		this(app, true);
	}

	@Subscribe
	public void onUIRefresh(UIRefreshEvent ev) {
		dirty = true;
	}

	@Subscribe
	public void onConnectionLost(ConnectionLostEvent ev) {
		application.setClient(null);

		if (application.isHost()) {
			final GameServer server = application.getServer();
			application.setServer(null);

			ThreadHandler.getInstance().executeRunnable(() -> server.stop());
		}

		application.getScreenManager().pushScreen("server_browser", null, true);
	}

	@Subscribe
	public void onServerReady(ServerReadyEvent event) {
		// TODO
		((GameRoundendScreen) application.getScreenManager()
				.getScreen("round_end")).setServerReady();
	}

	@Subscribe
	public void onRoundEnd(RoundEndEvent event) {
		// TODO
		application.getScreenManager().pushScreen("round_end", "circle_crop");
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
		if (dirty) {
			setUIValues();
			dirty = false;
		}

		super.render(delta);

		updateGame(delta);
	}

	public void updateGame(float delta) {
		if (application.getClient() == null)
			return;

		if (updateSession) {
			application.getClient().update();
			application.getClient().updatePing(Gdx.graphics.getDeltaTime());

			if (application.isHost())
				application.getServer().update();
		}
	}

	public abstract void renderGame(float delta);

}
