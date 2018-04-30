package de.gg.screen;

import java.util.HashMap;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Align;
import com.google.common.eventbus.Subscribe;

import de.gg.event.ConnectionEstablishedEvent;
import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerChangedEvent;
import de.gg.event.PlayerConnectedEvent;
import de.gg.event.PlayerDisconnectedEvent;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.type.GameMaps;
import de.gg.game.type.Religion;
import de.gg.input.ButtonClickListener;
import de.gg.network.GameClient;
import de.gg.network.LobbyPlayer;
import de.gg.ui.OffsetableTextField;
import de.gg.util.Log;
import de.gg.util.PlayerUtils;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class LobbyScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town2.jpg";
	@Asset(Texture.class)
	private final String READY_IMAGE_PATH = "ui/icons/ready.png";
	@Asset(Texture.class)
	private final String NOT_READY_IMAGE_PATH = "ui/icons/not_ready.png";
	@Asset(Texture.class)
	private final String KICK_IMAGE_PATH = "ui/icons/kick.png";
	@Asset(Texture.class)
	private final String NO_ICON_IMAGE_PATH = "ui/icons/players/empty.png";
	@Asset(Texture.class)
	private final String ICON1_IMAGE_PATH = "ui/icons/players/icon_1.png";
	@Asset(Texture.class)
	private final String ICON2_IMAGE_PATH = "ui/icons/players/icon_2.png";
	@Asset(Sound.class)
	private final String BUTTON_SOUND = "audio/button-tick.mp3";

	private Label messagesArea, settingsArea;
	private Table[] playerSlots;
	private ImageTextButton readyUpLobbyButton;
	private ScrollPane messagesPane;

	private GameSessionSetup sessionSetup;
	private HashMap<Short, LobbyPlayer> players;
	private short localNetworkId;

	@Override
	protected void initUI() {
		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		Sound clickSound = assetManager.get(BUTTON_SOUND);

		ImageTextButton playerSetingsButton = new ImageTextButton("Anpassen",
				skin, "small");
		playerSetingsButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);

				// TODO
				
				Dialog playerConfigurationDialog = new Dialog("Spielerkonfiguration", skin);
				Dialog iconDialog = new Dialog("Wappen", skin);
				
				TextField surnameTextField = new TextField(getLocalPlayer().getSurname(), skin);
				TextField nameTextField = new TextField(getLocalPlayer().getName(), skin);
				
				String sex = getLocalPlayer().isMale() ? "Männlich" : "Weiblich";
				ImageTextButton sexButton = new ImageTextButton("Geschlecht: " + sex , skin);
				
				ImageTextButton religionButton = new ImageTextButton("Religion: Katholisch", skin);
				
				ImageTextButton iconButton = new ImageTextButton("Wappen: ", skin);
				Table iconTable = new Table();
				for(int i = 0; i < de.gg.game.type.PlayerIcon.values().length;i++) {
				ImageButton iconIButton = new ImageButton(skin.getDrawable(de.gg.game.type.PlayerIcon.values()[i].getIconFileName()));
				final int index = i;
				iconIButton.addListener(
						new ButtonClickListener(assetManager, game.getSettings()) {
							
							@Override
							protected void onClick() {
								getLocalPlayer().setIcon(de.gg.game.type.PlayerIcon.values()[index]);
								
								updatePlayerSlot(playerSlots[localNetworkId], getLocalPlayer());
								game.getClient().onLocalPlayerChange(getLocalPlayer());
								updateLobbyUI();
								
								iconDialog.hide();
								
								
							}
						});
				iconTable.add(iconIButton).padLeft(50);
				}
				
				ImageTextButton applyButton = new ImageTextButton("Übernehmen", skin);
				ImageTextButton discardButton = new ImageTextButton("Abbrechen", skin);
				
				
				
				sexButton.addListener(
						new ButtonClickListener(assetManager, game.getSettings()) {

							@Override
							protected void onClick() {
								if(getLocalPlayer().isMale()) {
									getLocalPlayer().setMale(false);
									
									updatePlayerSlot(playerSlots[localNetworkId], getLocalPlayer());
									game.getClient().onLocalPlayerChange(getLocalPlayer());
									updateLobbyUI();
									
									sexButton.setText("Geschlecht: Weiblich");
								} else {
									getLocalPlayer().setMale(true);
									
									updatePlayerSlot(playerSlots[localNetworkId], getLocalPlayer());
									game.getClient().onLocalPlayerChange(getLocalPlayer());
									updateLobbyUI();
									
									sexButton.setText("Geschlecht: Männlich");
								}
								
							}
							
						}
						);
				
				religionButton.addListener(
						new ButtonClickListener(assetManager, game.getSettings()) {

							@Override
							protected void onClick() {
								if (getLocalPlayer().getReligion().equals(de.gg.game.type.Religion.values()[1])) {
									religionButton.setText("Religion: Katholisch");
									getLocalPlayer().setReligion(de.gg.game.type.Religion.values()[0]);
									
									updatePlayerSlot(playerSlots[localNetworkId], getLocalPlayer());
									game.getClient().onLocalPlayerChange(getLocalPlayer());
									updateLobbyUI();
									
								} else {
									religionButton.setText("Religion: Orthodox");
									getLocalPlayer().setReligion(de.gg.game.type.Religion.values()[1]);
									
									updatePlayerSlot(playerSlots[localNetworkId], getLocalPlayer());
									game.getClient().onLocalPlayerChange(getLocalPlayer());
									updateLobbyUI();
									
								}
								
							}
							
						}
						);
				
				iconButton.addListener(
						new ButtonClickListener(assetManager, game.getSettings()) {

							@Override
							protected void onClick() {
								stage.addActor(iconDialog);	
							}
							
						}
						);
				
				
				
				applyButton.addListener(
						new ButtonClickListener(assetManager, game.getSettings()) {
							
							@Override
							protected void onClick() {
								getLocalPlayer().setName(nameTextField.getText());
								getLocalPlayer().setSurname(surnameTextField.getText());
								updatePlayerSlot(playerSlots[localNetworkId], getLocalPlayer());
								game.getClient().onLocalPlayerChange(getLocalPlayer());
								updateLobbyUI();
								playerConfigurationDialog.hide();
							}
							
						});
				
				discardButton.addListener(new ButtonClickListener(assetManager, game.getSettings()) {

					@Override
					protected void onClick() {
						playerConfigurationDialog.hide();
						
					}
					
				});
				
				
				Table playerConfigurationTable = new Table();
				nameTextField.setWidth(200);
				playerConfigurationTable.add(nameTextField).row();
				surnameTextField.setWidth(200);
				playerConfigurationTable.add(surnameTextField).row();
				playerConfigurationTable.add(sexButton).row();
				playerConfigurationTable.add(religionButton).row();
				playerConfigurationTable.add(iconButton).row();
				playerConfigurationTable.add(applyButton).row();
				playerConfigurationTable.add(discardButton);
				
				
				iconDialog.add(iconTable).pad(20);
				iconDialog.setWidth(350);
				iconDialog.setHeight(250);
				iconDialog.setX(350);
				iconDialog.setY(175);
				
				
				
				playerConfigurationDialog.add(playerConfigurationTable).pad(100);
				playerConfigurationDialog.setWidth(680);
				playerConfigurationDialog.setHeight(480);
				playerConfigurationDialog.setX(300);
				playerConfigurationDialog.setY(125);
				stage.addActor(playerConfigurationDialog);
				

				return true;
			}
		});

		ImageTextButton leaveButton = new ImageTextButton("Verlassen", skin,
				"small");
		leaveButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);
				game.getClient().disconnect();
				if (game.isHost())
					game.getServer().stop();

				game.pushScreen("mainMenu");
				return true;
			}
		});

		readyUpLobbyButton = new ImageTextButton("Bereit", skin, "small");
		if (game.isHost()) {
			readyUpLobbyButton.setDisabled(true);
			readyUpLobbyButton.setTouchable(Touchable.disabled);
			readyUpLobbyButton.setText("Spiel starten");
		}
		readyUpLobbyButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);
				getLocalPlayer().toggleReady();
				game.getClient().onLocalPlayerChange(getLocalPlayer());

				updateLobbyUI();

				return true;
			}
		});

		settingsArea = new Label("", skin);
		settingsArea.setAlignment(Align.topLeft);
		settingsArea.setWrap(true);
		settingsArea.setText("Karte: "
				+ GameMaps.getByIndex(sessionSetup.getMapId()).getName() + " \n"
				+ "Schwierigkeit: " + sessionSetup.getDifficulty());

		Table playerTable = new Table();
		Table buttonTable = new Table();
		Table chatTable = new Table();

		buttonTable.add(playerSetingsButton).bottom().padBottom(18).row();
		buttonTable.add(readyUpLobbyButton).padBottom(18).row();
		buttonTable.add(leaveButton).padBottom(50);

		Table chatInputTable = new Table();
		ImageTextButton sendButton = new ImageTextButton("Senden", skin,
				"small");
		OffsetableTextField chatInputField = new OffsetableTextField("", skin,
				"large", 8);
		chatInputField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char key) {
				if (!textField.getText().isEmpty() && (key == (char) 13)) { // Enter
					clickSound.play(1F);

					game.getClient().sendChatMessage(chatInputField.getText());
					onNewChatMessage(new NewChatMessagEvent(localNetworkId,
							chatInputField.getText()));
					chatInputField.setText("");
				}
			}
		});
		sendButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (!chatInputField.getText().isEmpty()) {
					clickSound.play(1F);

					game.getClient().sendChatMessage(chatInputField.getText());
					onNewChatMessage(new NewChatMessagEvent(localNetworkId,
							chatInputField.getText()));
					chatInputField.setText("");
				}

				return true;
			}
		});

		messagesArea = new Label("", skin, "with-background");
		messagesArea.setAlignment(Align.topLeft);
		messagesArea.setWrap(true);

		messagesPane = new ScrollPane(messagesArea);
		messagesPane.setForceScroll(false, true);

		chatInputTable.add(chatInputField).left().width(325).padRight(15);
		chatInputTable.add(sendButton);

		chatTable.add(messagesPane).height(135).width(465).top().row();
		chatTable.add(chatInputTable).left().padTop(10).width(465).bottom();

		playerSlots = new Table[6];
		playerSlots[0] = new Table();
		playerSlots[1] = new Table();
		playerSlots[2] = new Table();
		playerSlots[3] = new Table();
		playerSlots[4] = new Table();
		playerSlots[5] = new Table();

		playerTable.add(playerSlots[0]).height(29).width(465).row();
		playerTable.add(playerSlots[1]).height(29).width(465).row();
		playerTable.add(playerSlots[2]).height(29).width(465).row();
		playerTable.add(playerSlots[3]).height(29).width(465).row();
		playerTable.add(playerSlots[4]).height(29).width(465).row();
		playerTable.add(playerSlots[5]).height(29).width(465).row();

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment1"));
		mTable.add(playerTable).width(465).height(185);
		mTable.add(settingsArea).width(155).height(185).row();
		mTable.add(chatTable).height(185).bottom();
		mTable.add(buttonTable).height(185);

		mainTable.add(mTable);

		updateLobbyUI();
	}

	/**
	 * Updates the lobby ui after player changes.
	 */
	private void updateLobbyUI() {
		Object[] playersArray = players.values().toArray();

		updatePlayerSlot(playerSlots[0],
				(playersArray.length >= 1 ? (LobbyPlayer) playersArray[0]
						: null));
		updatePlayerSlot(playerSlots[1],
				(playersArray.length >= 2 ? (LobbyPlayer) playersArray[1]
						: null));
		updatePlayerSlot(playerSlots[2],
				(playersArray.length >= 3 ? (LobbyPlayer) playersArray[2]
						: null));
		updatePlayerSlot(playerSlots[3],
				(playersArray.length >= 4 ? (LobbyPlayer) playersArray[3]
						: null));
		updatePlayerSlot(playerSlots[4],
				(playersArray.length >= 5 ? (LobbyPlayer) playersArray[4]
						: null));
		updatePlayerSlot(playerSlots[5],
				(playersArray.length >= 6 ? (LobbyPlayer) playersArray[5]
						: null));

		if (game.isHost()) {
			if (PlayerUtils.areAllPlayersReadyExcept(players.values(),
					getLocalPlayer())) {
				readyUpLobbyButton.setDisabled(false);
				readyUpLobbyButton.setTouchable(Touchable.enabled);
			} else {
				readyUpLobbyButton.setDisabled(true);
				readyUpLobbyButton.setTouchable(Touchable.disabled);
			}
		} else {
			if (getLocalPlayer().isReady()) {
				readyUpLobbyButton.setText("Nicht bereit");
			} else {
				readyUpLobbyButton.setText("Bereit");
			}
		}

		startGameIfReady();
	}

	private Table updatePlayerSlot(Table t, LobbyPlayer p) {
		t.clear();
		if (p == null) {
			t.add().width(25);
			t.add(new Label("[#D3D3D3FF] -- Frei -- ", skin)).width(350);
			t.add().width(50);
		} else {
			t.add().width(25); // Icon
			t.add(new Label(p.getName() + "  " // Der Lesbarkeit halber zwei
												// Leerzeichen verwenden
					+ p.getSurname().replace(" ", "  ") + " ", skin))
					.width(350);
			t.add().width(25); // Bereit
			t.add().width(25); // Kicken
		}

		return t;
	}

	/**
	 * Adds a chat message to the ui.
	 * 
	 * @param sender
	 *            The sender. Can be null for system messages.
	 * @param message
	 *            The actual message.
	 */
	private void addChatMessageToUI(LobbyPlayer sender, String message) {
		messagesArea.setText(messagesArea.getText()
				+ (sender == null ? "[#EFE22DFF]"
						: ("[#" + sender.getIcon().getColor() + "]"
								+ sender.getName() + " " + sender.getSurname()
								+ ": []"))
				+ message + (sender == null ? "[]" : "") + " \n");
		messagesPane.layout();
		messagesPane.scrollTo(0, 0, 0, 0);
	}

	/**
	 * Starts the game if all players are ready.
	 */
	private void startGameIfReady() {
		if (PlayerUtils.areAllPlayersReady(players.values())) {
			addChatMessageToUI(null, "Spiel startet...");
			game.getInputMultiplexer().removeInputProcessors();

			game.getClient().establishRMIConnection(players, sessionSetup);

			Log.info("Client", "Spiel wird geladen...");
			game.pushScreen("gameLoading");
		}
	}

	@Subscribe
	public void onNewChatMessage(NewChatMessagEvent event) {
		addChatMessageToUI(players.get(event.getPlayerId()),
				event.getMessage());
	}

	@Subscribe
	public void onPlayerChanged(PlayerChangedEvent event) {
		players.put(event.getNetworkId(), event.getPlayer());
		updateLobbyUI();
	}

	@Subscribe
	public void onPlayerDisconnect(PlayerDisconnectedEvent event) {
		addChatMessageToUI(null, players.get(event.getId()).getName()
				+ " hat das Spiel verlassen");
		players.remove(event.getId());
		updateLobbyUI();
	}

	@Subscribe
	public void onPlayerConnect(PlayerConnectedEvent event) {
		players.put(event.getNetworkId(), event.getPlayer());
		addChatMessageToUI(null,
				event.getPlayer().getName() + " ist dem Spiel beigetreten");
		updateLobbyUI();
	}

	public void setupLobby(ConnectionEstablishedEvent event) {
		players = event.getPlayers();
		localNetworkId = event.getId();
		sessionSetup = event.getSettings();
	}

	private LobbyPlayer getLocalPlayer() {
		return players.get(localNetworkId);
	}

}
