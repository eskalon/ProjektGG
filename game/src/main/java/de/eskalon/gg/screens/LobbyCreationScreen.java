package de.eskalon.gg.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.google.gson.reflect.TypeToken;

import de.damios.guacamole.ICallback;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.net.ServerSettings;
import de.eskalon.commons.net.SimpleGameServer;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.screens.AbstractEskalonUIScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.graphics.ui.actors.OffsettableTextField;
import de.eskalon.gg.graphics.ui.actors.dialogs.SimpleTextDialog;
import de.eskalon.gg.input.BackInputProcessor;
import de.eskalon.gg.input.BackInputProcessor.BackInputActorListener;
import de.eskalon.gg.input.ButtonClickListener;
import de.eskalon.gg.misc.PlayerUtils.PlayerTemplate;
import de.eskalon.gg.net.GameClient;
import de.eskalon.gg.net.GameServer;
import de.eskalon.gg.simulation.GameSetup;
import de.eskalon.gg.simulation.model.types.GameDifficulty;
import de.eskalon.gg.simulation.model.types.GameMap;

public class LobbyCreationScreen extends AbstractEskalonUIScreen {

	private ProjektGGApplicationContext appContext;
	private Skin skin;

	@Asset("ui/backgrounds/server_browser_screen.jpg")
	private @Inject Texture backgroundTexture;
	@Asset(value = "data/misc/player_presets.json", params = "array_list_player_stub")
	private @Inject JSON playerStubsJson;

	private SimpleTextDialog connectingDialog;
	private List<PlayerTemplate> playerStubs;

	public LobbyCreationScreen(SpriteBatch batch,
			EskalonScreenManager screenManager, Skin skin,
			ISoundManager soundManager,
			ProjektGGApplicationContext appContext) {
		super(batch);
		this.appContext = appContext;
		this.skin = skin;

		setImage(backgroundTexture);

		BackInputProcessor backInput = new BackInputProcessor() {
			@Override
			public void onBackAction() {
				screenManager.pushScreen(ServerBrowserScreen.class,
						"shortBlendingTransition");
			}
		};
		addInputProcessor(backInput);
		mainTable.addListener(new BackInputActorListener() {
			@Override
			public void onBackAction() {
				backInput.onBackAction();
			}
		});

		this.playerStubs = playerStubsJson
				.getData(new TypeToken<ArrayList<PlayerTemplate>>() {
				}.getType());

		Label nameLabel = new Label(Lang.get("screen.lobby_creation.name"),
				skin);
		Label portLabel = new Label(Lang.get("screen.lobby_creation.port"),
				skin);

		OffsettableTextField nameField = new OffsettableTextField("", skin, 6);
		OffsettableTextField portField = new OffsettableTextField(
				String.valueOf(SimpleGameServer.DEFAULT_PORT), skin, 6);
		portField.setTextFieldFilter(
				new TextField.TextFieldFilter.DigitsOnlyFilter());

		Label difficultyLabel = new Label(
				Lang.get("screen.lobby_creation.difficulty"), skin);
		CheckBox easyDifficultyCheckbox = new CheckBox(
				Lang.get(GameDifficulty.EASY), skin);
		CheckBox normalDifficultyCheckbox = new CheckBox(
				Lang.get(GameDifficulty.NORMAL), skin);
		normalDifficultyCheckbox.setChecked(true);
		CheckBox hardDifficultyCheckbox = new CheckBox(
				Lang.get(GameDifficulty.HARD), skin);
		ButtonGroup<CheckBox> speedGroup = new ButtonGroup<>();
		speedGroup.add(easyDifficultyCheckbox);
		speedGroup.add(normalDifficultyCheckbox);
		speedGroup.add(hardDifficultyCheckbox);

		ImageTextButton backButton = new ImageTextButton(
				Lang.get("ui.generic.back"), skin);
		backButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				screenManager.pushScreen(ServerBrowserScreen.class,
						"shortBlendingTransition");
			}
		});

		ImageTextButton createButton = new ImageTextButton(
				Lang.get("screen.lobby_creation.create"), skin);
		createButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				if (!nameField.getText().isEmpty()
						&& !portField.getText().isEmpty()) {
					GameDifficulty difficulty = GameDifficulty.NORMAL;

					if (speedGroup.getChecked()
							.equals(easyDifficultyCheckbox)) {
						difficulty = GameDifficulty.EASY;
					} else if (speedGroup.getChecked()
							.equals(normalDifficultyCheckbox)) {
						difficulty = GameDifficulty.NORMAL;
					} else if (speedGroup.getChecked()
							.equals(hardDifficultyCheckbox)) {
						difficulty = GameDifficulty.HARD;
					}

					// Start Sever & Client
					ServerSettings serverSetup = new ServerSettings(
							nameField.getText(), 7,
							Integer.valueOf(portField.getText()), true,
							appContext.getVersion(), true);
					GameSetup sessionSetup = new GameSetup(
							difficulty, GameMap.BAMBERG,
							System.currentTimeMillis());
					appContext.setServer(new GameServer(serverSetup,
							sessionSetup, null, playerStubs));
					appContext.getServer().start(new ICallback() {
						@Override
						public void onSuccess(Object param) {
							// Server successfully created; now we need to
							// establish a connection with our client
							appContext.setClient(EskalonInjector.instance()
									.getInstance(GameClient.class));
							appContext.getClient().connect(new ICallback() {
								// Sucessfully connected
								public void onSuccess(Object param) {
									connectingDialog.hide();
									LobbyScreen screen = EskalonInjector
											.instance()
											.getInstance(LobbyScreen.class);
									screen.setLobbyData((LobbyData) param);
									screenManager.pushScreen(screen,
											appContext.getTransitions()
													.get("blendingTransition"));
								};

								public void onFailure(Object param) {
									onHostStartingFailed(
											((Exception) param).getMessage());
								};
							}, appContext.getVersion(), "localhost",
									serverSetup.getPort());
						}

						public void onFailure(Object param) {
							onHostStartingFailed(
									((Exception) param).getMessage());
						};
					});

					connectingDialog = SimpleTextDialog.createAndShow(stage,
							skin,
							Lang.get(
									"screen.lobby_creation.starting_server.title"),
							Lang.get(
									"screen.lobby_creation.starting_server.text"),
							false, null);
					connectingDialog.show(stage);
				} else {
					SimpleTextDialog.createAndShow(stage, skin,
							Lang.get(
									"screen.lobby_creation.fields_empty.title"),
							Lang.get(
									"screen.lobby_creation.fields_empty.text"));
				}
			}
		});

		Table titleTable = new Table();
		Table settingsTable = new Table();
		Table settings2ColTable = new Table();
		Table settings3ColTable = new Table();
		Table buttonTable = new Table();

		titleTable.add(new Label(Lang.get("screen.lobby_creation.title"), skin,
				"title")).padTop(25);

		settings2ColTable.add(nameLabel).padLeft(100).padBottom(20).padRight(10)
				.padTop(30);
		settings2ColTable.add(nameField).row();
		settings2ColTable.add(portLabel).padLeft(100).padRight(10);
		settings2ColTable.add(portField).row();

		settings3ColTable.add(difficultyLabel).colspan(3).padLeft(150).row();
		settings3ColTable.add(easyDifficultyCheckbox).padLeft(150).padRight(12);
		settings3ColTable.add(normalDifficultyCheckbox).padRight(12);
		settings3ColTable.add(hardDifficultyCheckbox);

		settingsTable.left().top().add(settings2ColTable).padBottom(27).row();
		settingsTable.add(settings3ColTable).row();

		buttonTable.add(backButton);
		buttonTable.add(createButton).padLeft(65);

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment2"));
		mTable.add(titleTable).row();
		mTable.add(settingsTable).width(580).height(215).row();
		mTable.add(buttonTable).height(50).bottom().padBottom(50);

		mainTable.add(mTable);
	}

	public void onHostStartingFailed(String msg) {
		appContext.getServer().stop();
		appContext.setServer(null);
		appContext.setClient(null);

		connectingDialog.setVisible(false);
		SimpleTextDialog.createAndShow(stage, skin,
				Lang.get("ui.generic.error"), msg);
	}

}
