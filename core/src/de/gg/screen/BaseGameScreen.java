package de.gg.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.google.common.eventbus.Subscribe;

import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerDisconnectedEvent;
import de.gg.event.RoundEndEvent;
import de.gg.ui.AnimationlessDialog;

public abstract class BaseGameScreen extends BaseUIScreen {

	private final boolean updateGame;

	private AnimationlessDialog pauseDialog;

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
	protected void initUI() {
		pauseDialog = new AnimationlessDialog("Taste belegen", skin) {
			protected void result(Object object) {
				if (object.equals("settings")) {
					((SettingsScreen) game.getScreen("settings")).setCaller(getInstance());
					game.pushScreen("settings");
				} else {
					game.getNetworkHandler().disconnect();
					game.setCurrentSession(null);
					game.pushScreen("mainMenu");
				}
			};
		};
		pauseDialog.button("Settings", "settings").button("Verbindung trennen", "disconnect");
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
		((GameRoundendScreen) game.getScreen("roundEnd")).setData(event.getData());
	}

	@Override
	public void show() {
		super.show();
		super.stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE) {
					pauseDialog.show(stage);
				}
				return super.keyDown(event, keycode);
			}
		});
	}

	@Override
	public void render(float delta) {
		if (updateGame) {
			if (game.getCurrentSession().update()) {
				game.pushScreen("roundEnd");
			}

			game.getNetworkHandler().updatePing(delta);
			game.getNetworkHandler().updateServer();
		}

		Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (backgroundTexture != null) {
			game.getSpriteBatch().begin();
			game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);
			game.getSpriteBatch().draw(this.backgroundTexture, 0, 0, game.getViewportWidth(), game.getViewportHeight());
			game.getSpriteBatch().end();
		}

		renderGame(delta);

		stage.getBatch().setProjectionMatrix(game.getUICamera().combined);
		stage.act(delta);
		stage.draw();
	}

	private BaseGameScreen getInstance() {
		return this;
	}

	public abstract void renderGame(float delta);

}
