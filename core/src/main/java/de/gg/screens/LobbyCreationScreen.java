package de.gg.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.google.common.eventbus.Subscribe;
import com.google.gson.reflect.TypeToken;

import de.gg.events.ConnectionEstablishedEvent;
import de.gg.events.ConnectionFailedEvent;
import de.gg.game.GameSessionSetup;
import de.gg.game.types.GameDifficulty;
import de.gg.game.types.GameMap;
import de.gg.input.ButtonClickListener;
import de.gg.lang.Lang;
import de.gg.network.GameClient;
import de.gg.network.GameServer;
import de.gg.network.GameServer.IHostCallback;
import de.gg.network.ServerSetup;
import de.gg.ui.components.AnimationlessDialog;
import de.gg.ui.components.OffsetableTextField;
import de.gg.utils.Log;
import de.gg.utils.PlayerUtils;
import de.gg.utils.PlayerUtils.PlayerStub;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class LobbyCreationScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town.jpg";
	private AnimationlessDialog connectingDialog;
	private List<PlayerStub> playerStubs;

	@Override
	protected void onInit(AnnotationAssetManager assetManager) {
		super.onInit(assetManager);

		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		playerStubs = assetManager.get(PlayerUtils.PLAYER_PRESETS_JSON_PATH())
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
				String.valueOf(GameServer.DEFAULT_PORT), skin, 6);
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
							Log.info("Client",
									"--- Neues Spiel wird erstellt ---");
							Log.info("Server", "Server wird gestartet...");
							game.setServer(new GameServer(serverSetup,
									sessionSetup, null, playerStubs));
							game.getServer().start(new IHostCallback() {
								@Override
								public void onHostStarted(Exception e) {
									if (e == null) {
										// Connect client to server
										game.setClient(new GameClient(
												game.getEventBus()));
										game.getClient().connect(game.VERSION,
												"localhost",
												serverSetup.getPort());
									} else {
										game.getEventBus().post(
												new ConnectionFailedEvent(e));
									}
								}
							});

							connectingDialog = new AnimationlessDialog(Lang.get(
									"screen.lobby_creation.starting_server.title"),
									skin);
							connectingDialog.text(Lang.get(
									"screen.lobby_creation.starting_server.text"));
							connectingDialog.show(stage);
						} else {
							AnimationlessDialog dialog = new AnimationlessDialog(
									Lang.get(
											"screen.lobby_creation.fields_empty.title"),
									skin);
							dialog.text(Lang.get(
									"screen.lobby_creation.fields_empty.title"));
							dialog.button(Lang.get("ui.generic.ok"), true);
							dialog.key(Keys.ENTER, true);
							dialog.show(stage);
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
	public void onHostStarted(ConnectionEstablishedEvent event) {
		connectingDialog.setVisible(false);
		((LobbyScreen) game.getScreen("lobby")).setupLobby(event);
		game.pushScreen("lobby");
	}

	@Subscribe
	public void onHostStartingFailed(ConnectionFailedEvent event) {
		connectingDialog.setVisible(false);

		if (game.getServer() != null) {
			game.getServer().stop();
			game.setServer(null);
		}

		game.setClient(null);

		AnimationlessDialog dialog = new AnimationlessDialog("ui.generic.error",
				skin);
		if (event.getException() != null)
			dialog.text(event.getException().getMessage());
		else
			dialog.text(event.getServerRejectionMessage().getMessage());
		dialog.button("ui.generic.ok", true);
		dialog.key(Keys.ENTER, true);
		dialog.show(stage);
	}

}
