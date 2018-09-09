package de.gg.screens;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.common.eventbus.Subscribe;

import de.gg.events.DisconnectionEvent;
import de.gg.events.RoundEndEvent;
import de.gg.events.ServerReadyEvent;
import de.gg.input.ButtonClickListener;
import de.gg.lang.Lang;
import de.gg.network.GameServer;
import de.gg.ui.components.KeySelectionInputField;
import de.gg.ui.components.KeySelectionInputField.KeySelectionEventListener;
import de.gg.utils.Log;
import de.gg.utils.SimpleListener;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class SettingsScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town3.jpg";

	private BaseScreen caller;

	@Override
	protected void onInit(AnnotationAssetManager assetManager) {
		super.onInit(assetManager);

		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
	}

	@Override
	protected void initUI() {
		// VOLUME
		Label masterVolume = new Label(
				Lang.get("screen.settings.master_volume"), skin);
		Slider masterSlider = new Slider(0, 1, 0.05F, false, skin);
		masterSlider.setValue(game.getSettings().getUIVolumeLevel());
		masterSlider.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer,
					int button) {
				game.getSettings().setMasterVolume(masterSlider.getValue());
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}
		});

		Label effectVolume = new Label(
				Lang.get("screen.settings.effect_volume"), skin);
		Slider effectSlider = new Slider(0, 1, 0.05F, false, skin);
		effectSlider.setValue(game.getSettings().getEffectVolume());
		effectSlider.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer,
					int button) {
				game.getSettings().setEffectVolume(effectSlider.getValue());
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}
		});

		Label musicVolume = new Label(Lang.get("screen.settings.music_volume"),
				skin);
		Slider musicSlider = new Slider(0, 1, 0.05F, false, skin);
		musicSlider.setValue(game.getSettings().getMusicVolume());
		musicSlider.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer,
					int button) {
				game.getSettings().setMusicVolume(musicSlider.getValue());
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}
		});

		// KEYS
		Label forwardLabel = new Label(Lang.get("screen.settings.forward_key"),
				skin);
		KeySelectionInputField forwardButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getForwardKey()), skin, stage,
				buttonClickSound, game.getSettings(),
				new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setForwardKey(key);
					}
				});
		Label leftLabel = new Label(Lang.get("screen.settings.left_key"), skin);
		KeySelectionInputField leftButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getLeftKey()), skin, stage,
				buttonClickSound, game.getSettings(),
				new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setLeftKey(key);
					}
				});
		Label backwardLabel = new Label(
				Lang.get("screen.settings.backwards_key"), skin);
		KeySelectionInputField backwardButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getBackwardKey()), skin, stage,
				buttonClickSound, game.getSettings(),
				new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setBackwardKey(key);
					}
				});
		Label rightLabel = new Label(Lang.get("screen.settings.right_key"),
				skin);
		KeySelectionInputField rightButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getRightKey()), skin, stage,
				buttonClickSound, game.getSettings(),
				new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setRightKey(key);
					}
				});
		Label speedUpLabel = new Label(Lang.get("screen.settings.speed_up_key"),
				skin);
		KeySelectionInputField speedUpButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getSpeedUpKey()), skin, stage,
				buttonClickSound, game.getSettings(),
				new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setSpeedUpKey(key);
					}
				});
		Label speedDownLabel = new Label(
				Lang.get("screen.settings.speed_down_key"), skin);
		KeySelectionInputField speedDownButton = new KeySelectionInputField(
				Keys.toString(game.getSettings().getSpeedDownKey()), skin,
				stage, buttonClickSound, game.getSettings(),
				new KeySelectionEventListener() {
					@Override
					public void onKeySelection(int key) {
						game.getSettings().setSpeedDownKey(key);
					}
				});

		ImageTextButton backButton = new ImageTextButton(
				Lang.get("ui.generic.done"), skin, "small");
		backButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						if (caller instanceof GameMapScreen)
							game.pushScreen("map");
						else
							game.pushScreen("mainMenu");
					}
				});

		Table settingsTable = new Table();
		Table settings2ColTable = new Table();
		settings2ColTable.padTop(20);
		Table buttonTable = new Table();

		settings2ColTable.add(forwardLabel).padBottom(25);
		settings2ColTable.add(forwardButton).padBottom(25);

		settings2ColTable.add(masterVolume).padBottom(25).padLeft(70);
		settings2ColTable.add(masterSlider).padBottom(25).row();

		settings2ColTable.add(leftLabel).padBottom(25);
		settings2ColTable.add(leftButton).padBottom(25);

		settings2ColTable.add(effectVolume).padBottom(25).padLeft(70);
		settings2ColTable.add(effectSlider).padBottom(25).row();

		settings2ColTable.add(backwardLabel).padBottom(25);
		settings2ColTable.add(backwardButton).padBottom(25);

		settings2ColTable.add(musicVolume).padBottom(25).padLeft(70);
		settings2ColTable.add(musicSlider).padBottom(25).row();

		settings2ColTable.add(rightLabel).padBottom(50);
		settings2ColTable.add(rightButton).padBottom(50).row();

		settings2ColTable.add(speedUpLabel).padBottom(25);
		settings2ColTable.add(speedUpButton).padBottom(25).row();

		settings2ColTable.add(speedDownLabel).padBottom(25);
		settings2ColTable.add(speedDownButton).padBottom(25);

		settingsTable.left().top().add(settings2ColTable).row();

		buttonTable.add(backButton);

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment2"));
		mTable.add(settingsTable).width(580).height(405).padRight(70).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
	}

	@Override
	public void render(float delta) {
		// Wenn der SettingsScreen während des Spiels geöffnet ist, dieses
		// weiter updaten
		if (caller instanceof GameMapScreen) {
			if (game.getClient() != null) { // is not disconnecting
				game.getClient().update();

				game.getClient().updatePing(delta);
				if (game.isHost())
					game.getServer().update();
			}
		}
		super.render(delta);
	}

	@Subscribe
	public void onRoundEnd(RoundEndEvent event) {
		game.pushScreen("roundEnd");
	}

	@Subscribe
	public void onRoundEndDataArrived(ServerReadyEvent event) {
		((GameRoundendScreen) game.getScreen("roundEnd")).setServerReady();
	}

	/**
	 * @param event
	 * @see BaseGameScreen#onDisconnection(DisconnectionEvent)
	 */
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

			showInfoDialog(Lang.get("ui.generic.error"),
					Lang.get("ui.generic.disconnected"), true,
					new SimpleListener() {
						@Override
						public void listen(Object param) {
							game.pushScreen("mainMenu");
						}
					});
		}
	}

	/**
	 * @return the previously shown screen.
	 */
	public BaseScreen getCaller() {
		return caller;
	}

	public void setCaller(BaseScreen caller) {
		this.caller = caller;
	}
}
