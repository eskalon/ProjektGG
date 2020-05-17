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

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.JSON;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.utils.ISuccessCallback;
import de.gg.engine.network.BaseGameServer;
import de.gg.engine.network.ServerSetup;
import de.gg.engine.ui.components.OffsettableTextField;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.LobbyDataReceivedEvent;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.misc.PlayerUtils.PlayerStub;
import de.gg.game.model.types.GameDifficulty;
import de.gg.game.model.types.GameMap;
import de.gg.game.network.GameClient;
import de.gg.game.network.GameServer;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.ui.components.BasicDialog;

public class LobbyCreationScreen extends AbstractGGUIScreen {

	@Asset("ui/backgrounds/server_browser_screen.jpg")
	private Texture backgroundTexture;
	@Asset(value = "data/misc/player_presets.json", params = "array_list_player_stub")
	private JSON playerStubsJson;

	private OffsettableTextField nameField, portField;
	private CheckBox normalDifficultyCheckbox;

	private BasicDialog connectingDialog;
	private List<PlayerStub> playerStubs;

	public LobbyCreationScreen(ProjektGGApplication application) {
		super(application);
	}

	@Override
	protected void create() {
		super.create();

		setImage(backgroundTexture);
		this.playerStubs = playerStubsJson
				.getData(new TypeToken<ArrayList<PlayerStub>>() {
				}.getType());

		Label nameLabel = new Label(Lang.get("screen.lobby_creation.name"),
				skin);
		Label portLabel = new Label(Lang.get("screen.lobby_creation.port"),
				skin);

		nameField = new OffsettableTextField("", skin, 6);
		portField = new OffsettableTextField(
				String.valueOf(BaseGameServer.DEFAULT_PORT), skin, 6);
		portField.setTextFieldFilter(
				new TextField.TextFieldFilter.DigitsOnlyFilter());

		Label difficultyLabel = new Label(
				Lang.get("screen.lobby_creation.difficulty"), skin);
		CheckBox easyDifficultyCheckbox = new CheckBox(
				Lang.get(GameDifficulty.EASY), skin);
		normalDifficultyCheckbox = new CheckBox(Lang.get(GameDifficulty.NORMAL),
				skin);
		CheckBox hardDifficultyCheckbox = new CheckBox(
				Lang.get(GameDifficulty.HARD), skin);
		ButtonGroup<CheckBox> speedGroup = new ButtonGroup<>();
		speedGroup.add(easyDifficultyCheckbox);
		speedGroup.add(normalDifficultyCheckbox);
		speedGroup.add(hardDifficultyCheckbox);

		ImageTextButton backButton = new ImageTextButton(
				Lang.get("ui.generic.back"), skin, "small");
		backButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						application.getScreenManager().pushScreen(
								"server_browser", "shortBlendingTransition");
					}
				});

		ImageTextButton createButton = new ImageTextButton(
				Lang.get("screen.lobby_creation.create"), skin, "small");
		createButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
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
									application.VERSION, true);
							GameSessionSetup sessionSetup = new GameSessionSetup(
									difficulty, GameMap.BAMBERG,
									System.currentTimeMillis());
							application.setServer(new GameServer(serverSetup,
									sessionSetup, null, playerStubs));
							application.getServer()
									.start(new ISuccessCallback() {
										@Override
										public void onSuccess(Object param) {
											// Connect client to server
											application.setClient(
													new GameClient(application
															.getEventBus()));
											application.getClient().connect(
													new ISuccessCallback() {
														public void onSuccess(
																Object param) {
															// wait for
															// LobbyDataReceivedEvent
														};

														public void onFailure(
																Object param) {
															onHostStartingFailed(
																	((Exception) param)
																			.getMessage());
														};
													}, application.VERSION,
													"localhost",
													serverSetup.getPort());
										}

										public void onFailure(Object param) {
											onHostStartingFailed(
													((Exception) param)
															.getMessage());
										};
									});

							connectingDialog = new BasicDialog(Lang.get(
									"screen.lobby_creation.starting_server.title"),
									skin);
							connectingDialog.text(Lang.get(
									"screen.lobby_creation.starting_server.text"));
							connectingDialog.show(stage);
						} else {
							BasicDialog.createAndShow(stage, skin, Lang.get(
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

		titleTable.add(new Label("Lobby einrichten", skin, "title")).padTop(25);

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

	@Override
	protected void setUIValues() {
		nameField.setText("");
		portField.setText("");
		normalDifficultyCheckbox.setChecked(true);
	}

	public void onHostStartingFailed(String msg) {
		application.getServer().stop();
		application.setServer(null);
		application.setClient(null);

		connectingDialog.setVisible(false);
		BasicDialog.createAndShow(stage, skin, Lang.get("ui.generic.error"),
				msg);
	}

	@Subscribe
	public void onGameDataReceived(LobbyDataReceivedEvent ev) {
		connectingDialog.hide();
		application.getScreenManager().pushScreen("lobby",
				"blendingTransition");
	}

}
