package dev.gg.screen;

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

import dev.gg.network.MultiplayerSession;
import dev.gg.network.Player;
import dev.gg.network.event.ClientEventHandler;
import dev.gg.util.PlayerUtils;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class LobbyScreen extends BaseUIScreen implements ClientEventHandler {

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
	private MultiplayerSession session;

	@Override
	protected void initUI() {
		this.session = game.getCurrentMultiplayerSession();

		// backgroundTexture = assetManager.get(BACKGROUND_IMAGE_PATH);
		Sound clickSound = assetManager.get(BUTTON_SOUND);

		// mainTable.setBackground(skin.getDrawable("parchment-small"));

		ImageTextButton leaveButton = new ImageTextButton("Verlassen", skin
		/* "small" */);
		leaveButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);
				session.stop();
				game.setCurrentSession(null);

				game.pushScreen("mainMenu");
				return true;
			}
		});

		readyUpLobbyButton = new ImageTextButton("Bereit", skin /* "small" */);
		if (session.isHost()) {
			readyUpLobbyButton.setDisabled(true);
			readyUpLobbyButton.setText("Spiel starten");
		}
		readyUpLobbyButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				clickSound.play(1F);
				session.getPlayer().toggleReady();

				if (!session.isHost()) {
					if (session.getPlayer().isReady()) {
						readyUpLobbyButton.setText("Nicht bereit");
					} else {
						readyUpLobbyButton.setText("Bereit");
					}
				}
				session.onLocalPlayerChange();
				updateLobby();

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

					session.sendNewChatMessage(chatInputField.getText());
					onNewChatMessage(session.getLocalID(),
							chatInputField.getText());
					chatInputField.setText("");
				}
			}
		});

		sendButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (!chatInputField.getText().isEmpty()) {
					clickSound.play(1F);

					session.sendNewChatMessage(chatInputField.getText());
					onNewChatMessage(session.getLocalID(),
							chatInputField.getText());
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

		updateLobby();
	}

	private void updateLobby() {
		Object[] playersArray = session.getPlayers().values().toArray();

		updatePlayerSlot(playerSlots[0],
				(playersArray.length >= 1 ? (Player) playersArray[0] : null));
		updatePlayerSlot(playerSlots[1],
				(playersArray.length >= 2 ? (Player) playersArray[1] : null));
		updatePlayerSlot(playerSlots[2],
				(playersArray.length >= 3 ? (Player) playersArray[2] : null));
		updatePlayerSlot(playerSlots[3],
				(playersArray.length >= 4 ? (Player) playersArray[3] : null));
		updatePlayerSlot(playerSlots[4],
				(playersArray.length >= 5 ? (Player) playersArray[4] : null));
		updatePlayerSlot(playerSlots[5],
				(playersArray.length >= 6 ? (Player) playersArray[5] : null));

		if (session.isHost()) {
			if (PlayerUtils.areAllPlayersReadyExcept(
					session.getPlayers().values(), session.getPlayer())) {
				readyUpLobbyButton.setDisabled(false);
			} else {
				readyUpLobbyButton.setDisabled(true);
			}
		}

		startGameIfReady();
	}

	private void addChatMessage(String sender, String message) {
		messagesArea.appendText(
				((sender == null || sender.isEmpty()) ? "" : sender + ": ")
						+ message + " \n");
	}
	private void startGameIfReady() {
		if (PlayerUtils.areAllPlayersReady(session.getPlayers().values())) {
			addChatMessage(null, "Spiel startet...");

			// Input processor null?
			// pushScreen();
		}
	}

	@Override
	public void onNewChatMessage(int senderId, String message) {
		addChatMessage(
				session.getPlayers().get(senderId).getName() + " "
						+ session.getPlayers().get(senderId).getSurname(),
				message);
	}

	@Override
	public void onPlayerChanged() {
		updateLobby();
	}

	@Override
	public void onPlayerDisconnect(Player player) {
		addChatMessage(null, player.getName() + " hat das Spiel verlassen");
		updateLobby();
	}

	@Override
	public void onPlayerConnect(Player player) {
		addChatMessage(null, player.getName() + " ist dem Spiel beigetreten");
		updateLobby();
	}

	private Table updatePlayerSlot(Table t, Player p) {
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

}
