package de.gg.game.ui.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.TypeToken;

import de.gg.engine.asset.AnnotationAssetManager.InjectAsset;
import de.gg.engine.asset.JSON;
import de.gg.engine.lang.Lang;
import de.gg.engine.network.BaseGameServer;
import de.gg.engine.network.IClientConnectCallback;
import de.gg.engine.network.IHostCallback;
import de.gg.engine.network.ServerSetup;
import de.gg.engine.ui.components.OffsetableTextField;
import de.gg.game.events.GameDataReceivedEvent;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.network.GameClient;
import de.gg.game.network.GameServer;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.types.GameDifficulty;
import de.gg.game.types.GameMap;
import de.gg.game.ui.components.BasicDialog;
import de.gg.game.utils.PlayerUtils.PlayerStub;

public class LobbyCreationScreen extends BaseUIScreen {

	@InjectAsset("ui/backgrounds/town.jpg")
	private Texture backgroundImage;

	@InjectAsset(value = "data/misc/player_presets.json", params = "array_list_player_stub")
	private JSON playerStubsJson;

	private BasicDialog connectingDialog;
	private List<PlayerStub> playerStubs;

	@Override
	protected void onInit() {
		super.onInit();

		super.backgroundTexture = backgroundImage;
		this.playerStubs = playerStubsJson
				.getData(new TypeToken<ArrayList<PlayerStub>>() {
				}.getType());
	}

	@Override
	protected void initUI() {
		Label nameLabel = new Label(Lang.get("screen.lobby_creation.name"),
				skin);
		Label portLabel = new Label(Lang.get("screen.lobby_creation.port"),
				skin);

		OffsetableTextField nameField = new OffsetableTextField("", skin, 6);
		OffsetableTextField portField = new OffsetableTextField(
				String.valueOf(BaseGameServer.DEFAULT_PORT), skin, 6);
		portField.setTextFieldFilter(
				new TextField.TextFieldFilter.DigitsOnlyFilter());

		Label difficultyLabel = new Label(
				Lang.get("screen.lobby_creation.difficulty"), skin);
		CheckBox easyDifficultyCheckbox = new CheckBox(
				Lang.get(GameDifficulty.EASY), skin);
		CheckBox normalDifficultyCheckbox = new CheckBox(
				Lang.get(GameDifficulty.NORMAL), skin);
		CheckBox hardDifficultyCheckbox = new CheckBox(
				Lang.get(GameDifficulty.HARD), skin);
		ButtonGroup<CheckBox> speedGroup = new ButtonGroup<>();
		speedGroup.add(easyDifficultyCheckbox);
		speedGroup.add(normalDifficultyCheckbox);
		speedGroup.add(hardDifficultyCheckbox);
		normalDifficultyCheckbox.setChecked(true);

		ImageTextButton backButton = new ImageTextButton(
				Lang.get("ui.generic.back"), skin, "small");
		backButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						game.pushScreen("serverBrowser");
					}
				});

		ImageTextButton createButton = new ImageTextButton(
				Lang.get("screen.lobby_creation.create"), skin, "small");
		createButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
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
							ServerSetup serverSetup = new ServerSetup(
									nameField.getText(), 8,
									Integer.valueOf(portField.getText()), true,
									game.VERSION, true);
							GameSessionSetup sessionSetup = new GameSessionSetup(
									difficulty, GameMap.BAMBERG,
									System.currentTimeMillis());
							game.setServer(new GameServer(serverSetup,
									sessionSetup, null, playerStubs));
							game.getServer().start(new IHostCallback() {
								@Override
								public void onHostStarted(Exception e) {
									if (e == null) {
										// Connect client to server
										game.setClient(new GameClient(
												game.getEventBus()));
										game.getClient().connect(
												new IClientConnectCallback() {
													@Override
													public void onClientConnected(
															String errorMessage) {
														if (errorMessage == null) {
															connectingDialog
																	.setVisible(
																			false);
															showInfoDialog(Lang
																	.get("ui.generic.connecting"),
																	Lang.get(
																			"screen.server_browser.receiving"),
																	true);
														} else
															onHostStartingFailed(
																	errorMessage);
													}
												}, game.VERSION, "localhost",
												serverSetup.getPort());
									} else {
										onHostStartingFailed(e.getMessage());
									}
								}
							});

							connectingDialog = new BasicDialog(Lang.get(
									"screen.lobby_creation.starting_server.title"),
									skin);
							connectingDialog.text(Lang.get(
									"screen.lobby_creation.starting_server.text"));
							connectingDialog.show(stage);
						} else {
							showInfoDialog(Lang.get(
									"screen.lobby_creation.fields_empty.title"),
									Lang.get(
											"screen.lobby_creation.fields_empty.title"));
						}
					}
				});

		Table settingsTable = new Table();
		Table settings2ColTable = new Table();
		Table settings3ColTable = new Table();
		Table buttonTable = new Table();

		settings2ColTable.add(nameLabel).padBottom(30);
		settings2ColTable.add(nameField).padBottom(30).row();
		settings2ColTable.add(portLabel);
		settings2ColTable.add(portField).row();

		settings3ColTable.add(difficultyLabel).colspan(3).row();
		settings3ColTable.add(easyDifficultyCheckbox).padRight(6);
		settings3ColTable.add(normalDifficultyCheckbox).padRight(6);
		settings3ColTable.add(hardDifficultyCheckbox);

		settingsTable.left().top().add(settings2ColTable).padBottom(40).row();
		settingsTable.add(settings3ColTable).row();

		buttonTable.add(backButton);
		buttonTable.add(createButton).padLeft(65);

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment2"));
		mTable.add(settingsTable).width(580).height(405).row();
		mTable.add(buttonTable).height(50).bottom();

		mainTable.add(mTable);
	}

	@Subscribe
	public void onGameDataReceived(GameDataReceivedEvent ev) {
		connectingDialog.setVisible(false);
		((LobbyScreen) game.getScreen("lobby")).setupLobby(ev);
		game.pushScreen("lobby");
	}

	private void onHostStartingFailed(String errorMsg) {
		connectingDialog.setVisible(false);

		if (game.getServer() != null) {
			game.getServer().stop();
			game.setServer(null);
		}

		game.setClient(null);

		showInfoDialog(Lang.get("ui.generic.error"), errorMsg);
	}

}
