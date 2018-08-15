package de.gg.screen;

import java.util.HashMap;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Align;
import com.google.common.eventbus.Subscribe;

import de.gg.core.ProjektGG;
import de.gg.event.ConnectionEstablishedEvent;
import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerChangedEvent;
import de.gg.event.PlayerConnectedEvent;
import de.gg.event.PlayerDisconnectedEvent;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.type.GameMaps;
import de.gg.input.ButtonClickListener;
import de.gg.network.GameClient;
import de.gg.network.GameServer;
import de.gg.network.LobbyPlayer;
import de.gg.screen.dialog.PlayerLobbyConfigDialog;
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
	@Asset(Sound.class)
	private static final String CLICK_SOUND = "audio/button-tick.mp3";

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
		Sound clickSound = assetManager.get(CLICK_SOUND);

		PlayerLobbyConfigDialog playerConfigDialog = new PlayerLobbyConfigDialog(
				game, assetManager, skin, players, localNetworkId);

		ImageTextButton playerSettingsButton = new ImageTextButton("Anpassen",
				skin, "small");
		playerSettingsButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						playerConfigDialog
								.setLocalPlayerValues(getLocalPlayer());
						playerConfigDialog.show(stage);
					}
				});

		ImageTextButton leaveButton = new ImageTextButton("Verlassen", skin,
				"small");
		leaveButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						final GameClient client = game.getClient();
						game.setClient(null);
						final GameServer server = game.getServer();
						game.setServer(null);

						(new Thread(new Runnable() {
							@Override
							public void run() {
								client.disconnect();

								Log.info("Client", "Client beendet");

								if (server != null) {
									server.stop();
								}

								Log.info("Server", "Server beendet");
							}
						})).start();

						game.pushScreen("mainMenu");
					}
				});

		readyUpLobbyButton = new ImageTextButton("Bereit", skin, "small");
		if (game.isHost()) {
			readyUpLobbyButton.setDisabled(true);
			readyUpLobbyButton.setTouchable(Touchable.disabled);
			readyUpLobbyButton.setText("Spiel starten");
		}
		readyUpLobbyButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						getLocalPlayer().toggleReady();
						game.getClient().onLocalPlayerChange(getLocalPlayer());

						updateLobbyUI();
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

		buttonTable.add(playerSettingsButton).bottom().padBottom(18).row();
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
		sendButton.addListener(
				new ButtonClickListener(assetManager, game.getSettings()) {
					@Override
					protected void onClick() {
						game.getClient()
								.sendChatMessage(chatInputField.getText());
						onNewChatMessage(new NewChatMessagEvent(localNetworkId,
								chatInputField.getText()));
						chatInputField.setText("");
					}

					@Override
					protected boolean arePreconditionsMet() {
						return !chatInputField.getText().isEmpty();
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
		for (int i = 0; i < playerSlots.length; i++) {
			playerSlots[i] = new Table();
			playerTable.add(playerSlots[i]).height(29).width(465).row();
		}

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

		for (int i = 0; i < playerSlots.length; i++) {
			updatePlayerSlot(playerSlots[i],
					(playersArray.length >= (i + 1)
							? (LobbyPlayer) playersArray[i]
							: null));
		}

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

	/**
	 * Is called <i>before</i> this screen is
	 * {@linkplain ProjektGG#pushScreen(String) pushed} to setup the data of the
	 * current game.
	 * 
	 * @param event
	 */
	public void setupLobby(ConnectionEstablishedEvent event) {
		players = event.getPlayers();
		localNetworkId = event.getId();
		sessionSetup = event.getSettings();
	}

	private LobbyPlayer getLocalPlayer() {
		return players.get(localNetworkId);
	}

}
