package de.gg.game.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.LeftClickSlider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.common.eventbus.Subscribe;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.settings.EskalonSettings;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.ConnectionLostEvent;
import de.gg.game.events.RoundEndEvent;
import de.gg.game.events.ServerReadyEvent;
import de.gg.game.input.BackInputProcessor;
import de.gg.game.input.BackInputProcessor.BackInputActorListener;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.ui.components.KeySelectionInputField;

public class SettingsScreen extends AbstractGGUIScreen {

	@Asset("ui/backgrounds/settings_screen.jpg")
	private Texture backgroundImage1;
	@Asset("ui/backgrounds/main_menu_screen.png")
	private Texture backgroundImage2;

	public SettingsScreen(ProjektGGApplication application) {
		super(application);
	}

	@Override
	protected void create() {
		super.create();

		BackInputProcessor backInput = new BackInputProcessor() {
			@Override
			public void onBackAction() {
				if (application.getScreenManager()
						.getLastScreen() instanceof GameMapScreen)
					application.getScreenManager().pushScreen("map",
							"blendingTransition");
				else
					application.getScreenManager().pushScreen("main_menu",
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

		EskalonSettings settings = application.getSettings();

		// VOLUME
		Label masterVolume = new Label(
				Lang.get("screen.settings.master_volume"), skin);
		LeftClickSlider masterSlider = new LeftClickSlider(0, 1, 0.05F, false,
				skin);
		masterSlider.setValue(settings.getMasterVolume());
		masterSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				settings.setMasterVolume(masterSlider.getValue());
				application.getSoundManager()
						.setMasterVolume(masterSlider.getValue());
			}
		});

		Label effectVolume = new Label(
				Lang.get("screen.settings.effect_volume"), skin);
		LeftClickSlider effectSlider = new LeftClickSlider(0, 1, 0.05F, false,
				skin);
		effectSlider.setValue(settings.getEffectVolume());
		effectSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				settings.setEffectVolume(effectSlider.getValue());
				application.getSoundManager()
						.setEffectVolume(effectSlider.getValue());
			}
		});

		Label musicVolume = new Label(Lang.get("screen.settings.music_volume"),
				skin);
		LeftClickSlider musicSlider = new LeftClickSlider(0, 1, 0.05F, false,
				skin);
		musicSlider.setValue(settings.getMusicVolume());
		musicSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				settings.setMusicVolume(musicSlider.getValue());
				application.getSoundManager()
						.setMusicVolume(musicSlider.getValue());
			}
		});

		// KEYS
		Label forwardLabel = new Label(Lang.get("screen.settings.forward_key"),
				skin);
		KeySelectionInputField forwardButton = new KeySelectionInputField(
				settings, "cameraForward", skin, stage,
				application.getSoundManager());
		Label leftLabel = new Label(Lang.get("screen.settings.left_key"), skin);
		KeySelectionInputField leftButton = new KeySelectionInputField(settings,
				"cameraLeft", skin, stage, application.getSoundManager());
		Label backwardLabel = new Label(
				Lang.get("screen.settings.backwards_key"), skin);
		KeySelectionInputField backwardButton = new KeySelectionInputField(
				settings, "cameraBackward", skin, stage,
				application.getSoundManager());
		Label rightLabel = new Label(Lang.get("screen.settings.right_key"),
				skin);
		KeySelectionInputField rightButton = new KeySelectionInputField(
				settings, "cameraRight", skin, stage,
				application.getSoundManager());
		Label speedUpLabel = new Label(Lang.get("screen.settings.speed_up_key"),
				skin);
		KeySelectionInputField speedUpButton = new KeySelectionInputField(
				settings, "speedUpTime", skin, stage,
				application.getSoundManager());
		Label speedDownLabel = new Label(
				Lang.get("screen.settings.speed_down_key"), skin);
		KeySelectionInputField speedDownButton = new KeySelectionInputField(
				settings, "speedDownTime", skin, stage,
				application.getSoundManager());

		ImageTextButton backButton = new ImageTextButton(
				Lang.get("ui.generic.done"), skin);
		backButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						if (application.getScreenManager()
								.getLastScreen() instanceof GameMapScreen)
							application.getScreenManager().pushScreen("map",
									"blendingTransition");
						else
							application.getScreenManager().pushScreen(
									"main_menu", "blendingTransition");
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
	protected void setUIValues() {
		setImage((application.getScreenManager()
				.getLastScreen() instanceof GameMapScreen) ? backgroundImage1
						: backgroundImage2);
	}

	@Override
	public void render(float delta) {
		// If the settings are open while a game is running keep on updating it
		if (application.getScreenManager()
				.getLastScreen() instanceof GameMapScreen) {
			((GameMapScreen) application.getScreenManager().getLastScreen())
					.updateGame(delta);
		}
		super.render(delta);
	}

	@Subscribe
	public void onRoundEnd(RoundEndEvent event) {
		application.getScreenManager().pushScreen("round_end", "circle_crop");
	}

	@Subscribe
	public void onRoundEndDataArrived(ServerReadyEvent event) {
		((GameRoundendScreen) application.getScreenManager()
				.getScreen("round_end")).setServerReady();
	}

	@Subscribe
	public void onConnectionLost(ConnectionLostEvent ev) {
		((GameMapScreen) application.getScreenManager().getLastScreen())
				.onConnectionLost(ev);
	}

}
