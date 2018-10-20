package de.gg.screens;

import java.util.HashMap;

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
import de.gg.events.ConnectionEstablishedEvent;
import de.gg.events.NewChatMessagEvent;
import de.gg.events.PlayerChangedEvent;
import de.gg.events.PlayerConnectedEvent;
import de.gg.events.PlayerDisconnectedEvent;
import de.gg.game.GameSessionSetup;
import de.gg.input.ButtonClickListener;
import de.gg.lang.Lang;
import de.gg.network.GameClient;
import de.gg.network.GameServer;
import de.gg.network.LobbyPlayer;
import de.gg.screens.dialogs.PlayerLobbyConfigDialog;
import de.gg.ui.components.OffsetableTextField;
import de.gg.utils.PlayerUtils;
import de.gg.utils.log.Log;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
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

	private Label messagesArea, settingsArea;
	private Table[] playerSlots;
	private ImageTextButton readyUpLobbyButton;
	private ScrollPane messagesPane;

	private GameSessionSetup sessionSetup;
	private HashMap<Short, LobbyPlayer> players;
	private short localNetworkId;

	@Override
	protected void onInit(AnnotationAssetManager assetManager) {
		super.onInit(assetManager);

		backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
	}

	@Override
	protected void initUI() {
		PlayerLobbyConfigDialog playerConfigDialog = new PlayerLobbyConfigDialog(
				game, buttonClickSound, skin, players, localNetworkId);

		ImageTextButton playerSettingsButton = new ImageTextButton(
				Lang.get("screen.lobby.configure"), skin, "small");
		playerSettingsButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						playerConfigDialog
								.setLocalPlayerValues(getLocalPlayer());
						playerConfigDialog.show(stage);
					}
				});

		ImageTextButton leaveButton = new ImageTextButton(
				Lang.get("screen.lobby.disconnect"), skin, "small");
		leaveButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
					@Override
					protected void onClick() {
						final GameClient client = game.getClient();
						game.setClient(null);
						final GameServer server = game.getServer();
						game.setServer(null);

						(new Thread(() -> {
							client.disconnect();

							Log.info("Client", "Client beendet");

							if (server != null) {
								server.stop();
							}

							Log.info("Server", "Server beendet");
						})).start();

						game.pushScreen("mainMenu");
					}
				});

		readyUpLobbyButton = new ImageTextButton(Lang.get("screen.lobby.ready"),
				skin, "small");
		if (game.isHost()) {
			readyUpLobbyButton.setDisabled(true);
			readyUpLobbyButton.setTouchable(Touchable.disabled);
			readyUpLobbyButton.setText(Lang.get("screen.lobby.start_game"));
		}
		readyUpLobbyButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
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
		settingsArea.setText(Lang.get("screen.lobby.map", sessionSetup.getMap())
				+ " \n" + Lang.get("screen.lobby.difficulty",
						sessionSetup.getDifficulty()));

		Table playerTable = new Table();
		Table buttonTable = new Table();
		Table chatTable = new Table();

		buttonTable.add(playerSettingsButton).bottom().padBottom(18).row();
		buttonTable.add(readyUpLobbyButton).padBottom(18).row();
		buttonTable.add(leaveButton).padBottom(50);

		Table chatInputTable = new Table();
		ImageTextButton sendButton = new ImageTextButton(
				Lang.get("screen.lobby.send"), skin, "small");
		OffsetableTextField chatInputField = new OffsetableTextField("", skin,
				"large", 8);
		chatInputField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char key) {
				if (!textField.getText().isEmpty() && (key == (char) 13)) { // Enter
					buttonClickSound.play(1F);

					game.getClient().sendChatMessage(chatInputField.getText());
					onNewChatMessage(new NewChatMessagEvent(localNetworkId,
							chatInputField.getText()));
					chatInputField.setText("");
				}
			}
		});
		sendButton.addListener(
				new ButtonClickListener(buttonClickSound, game.getSettings()) {
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
				readyUpLobbyButton.setText(Lang.get("screen.lobby.not_ready"));
			} else {
				readyUpLobbyButton.setText(Lang.get("screen.lobby.ready"));
			}
		}

		startGameIfReady();
	}

	private Table updatePlayerSlot(Table t, LobbyPlayer p) {
		t.clear();
		if (p == null) {
			t.add().width(25);
			t.add(new Label("[#D3D3D3FF]" + Lang.get("screen.lobby.free_slot"),
					skin)).width(350);
			t.add().width(50);
		} else {
			t.add().width(25); // Icon
			t.add(new Label(Lang.get(p).replace(" ", "  "), // Der Lesbarkeit
															// halber zwei
															// Leerzeichen
															// verwenden
					skin)).width(350);
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
								+ Lang.get(sender) + ": []"))
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
		addChatMessageToUI(null, Lang.get("screen.lobby.player_left",
				players.get(event.getId())));
		players.remove(event.getId());
		updateLobbyUI();
	}

	@Subscribe
	public void onPlayerConnect(PlayerConnectedEvent event) {
		players.put(event.getNetworkId(), event.getPlayer());
		addChatMessageToUI(null,
				Lang.get("screen.lobby.player_joined", event.getPlayer()));
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
		localNetworkId = event.getNetworkId();
		sessionSetup = event.getSettings();
	}

	private LobbyPlayer getLocalPlayer() {
		return players.get(localNetworkId);
	}

}
