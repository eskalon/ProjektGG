package de.eskalon.gg.screens;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.event.Subscribe;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.screens.AbstractEskalonUIScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.events.ChatMessageEvent;
import de.eskalon.gg.events.ConnectionLostEvent;
import de.eskalon.gg.events.LobbyDataChangedEvent;
import de.eskalon.gg.graphics.ui.actors.KeySelectionInputField;
import de.eskalon.gg.graphics.ui.actors.KeySelectionInputField.BindingType;
import de.eskalon.gg.input.BackInputProcessor;
import de.eskalon.gg.input.BackInputProcessor.BackInputActorListener;
import de.eskalon.gg.input.ButtonClickListener;
import de.eskalon.gg.screens.game.MapScreen;
import de.eskalon.gg.screens.game.MapScreen.GameMapAxisBinding;
import de.eskalon.gg.screens.game.MapScreen.GameMapBinaryBinding;
import de.eskalon.gg.screens.game.RoundEndScreen;

public class SettingsScreen extends AbstractEskalonUIScreen {

	private @Inject ProjektGGApplicationContext appContext;
	private @Inject EskalonScreenManager screenManager;
	private @Inject Skin skin;
	private @Inject EskalonSettings settings;
	private @Inject ISoundManager soundManager;

	@Asset("ui/backgrounds/settings_screen.jpg")
	private @Inject Texture backgroundImage1;
	@Asset("ui/backgrounds/main_menu_screen.png")
	private @Inject Texture backgroundImage2;

	@Override
	public void show() {
		super.show();

		setImage(appContext.isInGame() ? backgroundImage1 : backgroundImage2);

		BackInputProcessor backInput = new BackInputProcessor() {
			@Override
			public void onBackAction() {
				if (appContext.isInGame())
					screenManager.pushScreen(MapScreen.class,
							"blendingTransition");
				else
					screenManager.pushScreen(MainMenuScreen.class,
							"blendingTransition");
			}
		};
		addInputProcessor(backInput);
		mainTable.addListener(new BackInputActorListener() {
			@Override
			public void onBackAction() {
				backInput.onBackAction();
			}
		});

		// VOLUME
		Label masterVolume = new Label(
				Lang.get("screen.settings.master_volume"), skin);
		Slider masterSlider = new Slider(0, 1, 0.05F, false, skin);
		masterSlider.setButton(Buttons.LEFT);
		masterSlider.setValue(soundManager.getMasterVolume());
		masterSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				soundManager.setMasterVolume(masterSlider.getValue());
			}
		});

		Label effectVolume = new Label(
				Lang.get("screen.settings.effect_volume"), skin);
		Slider effectSlider = new Slider(0, 1, 0.05F, false, skin);
		effectSlider.setButton(Buttons.LEFT);
		effectSlider.setValue(soundManager.getEffectVolume());
		effectSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				soundManager.setEffectVolume(effectSlider.getValue());
			}
		});

		Label musicVolume = new Label(Lang.get("screen.settings.music_volume"),
				skin);
		Slider musicSlider = new Slider(0, 1, 0.05F, false, skin);
		musicSlider.setButton(Buttons.LEFT);
		musicSlider.setValue(soundManager.getMusicVolume());
		musicSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				soundManager.setMusicVolume(musicSlider.getValue());
			}
		});

		// KEYS
		Label forwardLabel = new Label(Lang.get("screen.settings.forward_key"),
				skin);
		KeySelectionInputField forwardButton = new KeySelectionInputField(
				settings, GameMapAxisBinding.MOVE_FORWARDS_BACKWARDS,
				BindingType.AXIS_MAX, skin, stage, soundManager);
		Label leftLabel = new Label(Lang.get("screen.settings.left_key"), skin);
		KeySelectionInputField leftButton = new KeySelectionInputField(settings,
				GameMapAxisBinding.MOVE_LEFT_RIGHT, BindingType.AXIS_MIN, skin,
				stage, soundManager);
		Label backwardLabel = new Label(
				Lang.get("screen.settings.backwards_key"), skin);
		KeySelectionInputField backwardButton = new KeySelectionInputField(
				settings, GameMapAxisBinding.MOVE_FORWARDS_BACKWARDS,
				BindingType.AXIS_MIN, skin, stage, soundManager);
		Label rightLabel = new Label(Lang.get("screen.settings.right_key"),
				skin);
		KeySelectionInputField rightButton = new KeySelectionInputField(
				settings, GameMapAxisBinding.MOVE_LEFT_RIGHT,
				BindingType.AXIS_MAX, skin, stage, soundManager);
		Label speedUpLabel = new Label(Lang.get("screen.settings.speed_up_key"),
				skin);
		KeySelectionInputField speedUpButton = new KeySelectionInputField(
				settings, GameMapBinaryBinding.INCREASE_SPEED,
				BindingType.BINARY, skin, stage, soundManager);
		Label speedDownLabel = new Label(
				Lang.get("screen.settings.speed_down_key"), skin);
		KeySelectionInputField speedDownButton = new KeySelectionInputField(
				settings, GameMapBinaryBinding.DECREASE_SPEED,
				BindingType.BINARY, skin, stage, soundManager);

		ImageTextButton backButton = new ImageTextButton(
				Lang.get("ui.generic.done"), skin);
		backButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				if (screenManager.getLastScreen() instanceof MapScreen)
					screenManager.pushScreen(MapScreen.class,
							"blendingTransition");
				else
					screenManager.pushScreen(MainMenuScreen.class,
							"blendingTransition");
			}
		});

		Table settingsTable = new Table();
		Table settings2ColTable = new Table();
		settings2ColTable.padTop(20);
		Table buttonTable = new Table();

		settings2ColTable.add(forwardLabel).padBottom(22).left();
		settings2ColTable.add(forwardButton).padBottom(22);

		settings2ColTable.add(masterVolume).padBottom(28).padLeft(75)
				.padRight(10);
		settings2ColTable.add(masterSlider).padBottom(22).row();

		settings2ColTable.add(leftLabel).padBottom(22).left();
		settings2ColTable.add(leftButton).padBottom(22);

		settings2ColTable.add(effectVolume).padBottom(28).padLeft(75)
				.padRight(10);
		settings2ColTable.add(effectSlider).padBottom(22).row();

		settings2ColTable.add(backwardLabel).padBottom(22).left();
		settings2ColTable.add(backwardButton).padBottom(22);

		settings2ColTable.add(musicVolume).padBottom(28).padLeft(75)
				.padRight(10);
		settings2ColTable.add(musicSlider).padBottom(22).row();

		settings2ColTable.add(rightLabel).padBottom(55).left();
		settings2ColTable.add(rightButton).padBottom(55).row();

		settings2ColTable.add(speedUpLabel).padBottom(22).padRight(13).left();
		settings2ColTable.add(speedUpButton).padBottom(22).row();

		settings2ColTable.add(speedDownLabel).padBottom(22).padRight(13).left();
		settings2ColTable.add(speedDownButton).padBottom(22);

		settingsTable.left().top().add(settings2ColTable).row();

		buttonTable.add(backButton);

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment2"));
		mTable.add(settingsTable).width(580).height(405).padRight(90).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
	}

	@Override
	public void render(float delta) {
		// If the settings are open while a game is running keep on updating it
		if (appContext.isInGame()) {
			if (appContext.getGameHandler().update(delta))
				screenManager.pushScreen(RoundEndScreen.class, "circle_crop");
		}
		super.render(delta);
	}

	@Subscribe
	public void onLobbyDataChangedEvent(LobbyDataChangedEvent ev) {
		// TODO do stuff; see AbstractGameScreen
	}

	@Subscribe
	public void onChatMessageEvent(ChatMessageEvent<?> event) {
		// TODO do stuff; see AbstractGameScreen
	}

	@Subscribe
	public void onConnectionLost(ConnectionLostEvent ev) {
		if (this == screenManager.getCurrentScreen()) { // If this screen is
														// rendered as part of a
														// transition, the next
														// screen is responsible
														// for handling the lost
														// connection
			appContext.handleDisconnection();

			ServerBrowserScreen screen = EskalonInjector.instance()
					.getInstance(ServerBrowserScreen.class);
			screen.setJustDisconnectedFromServer(true);
			screenManager.pushScreen(screen, null);
		}
	}

}
