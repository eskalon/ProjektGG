package dev.gg.screen;

import java.util.HashMap;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.google.common.eventbus.Subscribe;

import de.gg.event.GameSessionSetupEvent;
import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerChangedEvent;
import de.gg.event.PlayerConnectedEvent;
import de.gg.event.PlayerDisconnectedEvent;
import dev.gg.core.LobbyPlayer;
import dev.gg.core.MultiplayerSession;
import dev.gg.data.GameSessionSetup;
import dev.gg.network.NetworkHandler;
import dev.gg.util.PlayerUtils;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class LobbyScreen extends BaseUIScreen {

	@Asset(Texture.class)
	private final String BACKGROUND_IMAGE_PATH = "ui/backgrounds/town.jpg";
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
	private TextArea messagesArea;
	private Table[] playerSlots;
	private ImageTextButton readyUpLobbyButton;

	private GameSessionSetup sessionSetup;
	private HashMap<Short, LobbyPlayer> players;
	private short localNetworkId;

	@Override
	protected void initUI() {
		// backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		Sound clickSound = assetManager.get(BUTTON_SOUND);

		// mainTable.setBackground(skin.getDrawable("parchment-small"));

		ImageTextButton leaveButton = new ImageTextButton("Verlassen", skin
		/* "small" */);
		leaveButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);
				game.getNetworkHandler().disconnect();

				game.pushScreen("mainMenu");
				return true;
			}
		});

		NetworkHandler netHandler = game.getNetworkHandler();

		readyUpLobbyButton = new ImageTextButton("Bereit", skin /* "small" */);
		if (netHandler.isHost()) {
			readyUpLobbyButton.setDisabled(true);
			readyUpLobbyButton.setText("Spiel starten");
		}
		readyUpLobbyButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);
				getLocalPlayer().toggleReady();
				netHandler.onLocalPlayerChange(getLocalPlayer());

				updateLobbyUI();

				return true;
			}
		});

		Table playerTable = new Table();
		Table settingsTable = new Table();
		Table buttonTable = new Table();
		Table chatTable = new Table();

		// settings table + player table

		buttonTable.add(readyUpLobbyButton).bottom().padBottom(20).row();
		buttonTable.add(leaveButton);

		Table chatInputTable = new Table();
		ImageTextButton sendButton = new ImageTextButton("Senden", skin);
		TextField chatInputField = new TextField("", skin);
		chatInputField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char key) {
				if (!textField.getText().isEmpty() && (key == (char) 13)) { // Enter
					clickSound.play(1F);

					netHandler.sendChatMessage(chatInputField.getText());
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

					netHandler.sendChatMessage(chatInputField.getText());
					onNewChatMessage(new NewChatMessagEvent(localNetworkId,
							chatInputField.getText()));
					chatInputField.setText("");
				}

				return true;
			}
		});

		messagesArea = new TextArea("", skin);
		messagesArea.setDisabled(true);
		ScrollPane messagesPane = new ScrollPane(messagesArea);

		chatInputTable.add(chatInputField).left().width(325).padRight(15);
		chatInputTable.add(sendButton);

		chatTable.debug();
		chatTable.add(messagesPane).height(125).width(425).top().row();
		chatTable.add(chatInputTable).left().padTop(10).width(425).bottom();

		playerSlots = new Table[6];
		playerSlots[0] = new Table();
		playerSlots[1] = new Table();
		playerSlots[2] = new Table();
		playerSlots[3] = new Table();
		playerSlots[4] = new Table();
		playerSlots[5] = new Table();

		playerTable.add(playerSlots[0]).height(25).width(425).row();
		playerTable.add(playerSlots[1]).height(25).width(425).row();
		playerTable.add(playerSlots[2]).height(25).width(425).row();
		playerTable.add(playerSlots[3]).height(25).width(425).row();
		playerTable.add(playerSlots[4]).height(25).width(425).row();
		playerTable.add(playerSlots[5]).height(25).width(425).row();

		mainTable.add(playerTable).width(425).height(155);
		mainTable.add(settingsTable).width(155).row();
		mainTable.add(chatTable).height(165).bottom();
		mainTable.add(buttonTable).height(165);

		mainTable.setDebug(true);

		// updateLobbyUI();
	}

	/**
	 * Updates the lobby ui after player changes.
	 */
	private void updateLobbyUI() {
		Object[] playersArray = players.values().toArray();

		updatePlayerSlot(playerSlots[0],
				(playersArray.length >= 1
						? (LobbyPlayer) playersArray[0]
						: null));
		updatePlayerSlot(playerSlots[1],
				(playersArray.length >= 2
						? (LobbyPlayer) playersArray[1]
						: null));
		updatePlayerSlot(playerSlots[2],
				(playersArray.length >= 3
						? (LobbyPlayer) playersArray[2]
						: null));
		updatePlayerSlot(playerSlots[3],
				(playersArray.length >= 4
						? (LobbyPlayer) playersArray[3]
						: null));
		updatePlayerSlot(playerSlots[4],
				(playersArray.length >= 5
						? (LobbyPlayer) playersArray[4]
						: null));
		updatePlayerSlot(playerSlots[5],
				(playersArray.length >= 6
						? (LobbyPlayer) playersArray[5]
						: null));

		if (game.getNetworkHandler().isHost()) {
			if (PlayerUtils.areAllPlayersReadyExcept(players.values(),
					getLocalPlayer())) {
				readyUpLobbyButton.setDisabled(false);
			} else {
				readyUpLobbyButton.setDisabled(true);
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
			t.add(new Label(" -- Frei -- ", skin)).width(350);
			t.add().width(50);
		} else {
			t.add().width(25); // Icon
			t.add(new Label(p.getName() + " " + p.getSurname() + " ", skin))
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
	private void addChatMessageToUI(String sender, String message) {
		messagesArea.appendText(
				((sender == null || sender.isEmpty()) ? "" : sender + ": ")
						+ message + " \n");
	}

	/**
	 * Starts the game if all players are ready.
	 */
	private void startGameIfReady() {
		if (PlayerUtils.areAllPlayersReady(players.values())) {
			addChatMessageToUI(null, "Spiel startet...");
			game.getInputMultiplexer().removeInputProcessors();
			game.setCurrentSession(new MultiplayerSession(sessionSetup, players,
					localNetworkId));
			game.getEventBus().register(game.getCurrentMultiplayerSession());
			game.getCurrentMultiplayerSession().startGame(game);
			game.pushScreen("gameLoading");
		}
	}

	@Subscribe
	public void onNewChatMessage(NewChatMessagEvent event) {
		addChatMessageToUI(
				players.get(event.getPlayerId()).getName() + " "
						+ players.get(event.getPlayerId()).getSurname(),
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

	@Subscribe
	public void onGameSetup(GameSessionSetupEvent event) {
		players = event.getPlayers();
		localNetworkId = event.getId();
		sessionSetup = event.getSettings();

		updateLobbyUI();
	}

	private LobbyPlayer getLocalPlayer() {
		return players.get(localNetworkId);
	}

}
