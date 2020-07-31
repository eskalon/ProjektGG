package de.gg.game.ui.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.google.common.eventbus.Subscribe;

import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.Log;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.lang.Lang;
import de.gg.engine.ui.components.OffsettableTextField;
import de.gg.game.core.ProjektGGApplication;
import de.gg.game.events.UIRefreshEvent;
import de.gg.game.input.ButtonClickListener;
import de.gg.game.misc.PlayerUtils;
import de.gg.game.network.GameClient;
import de.gg.game.network.GameServer;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.ui.data.ChatMessage;
import de.gg.game.ui.dialogs.PlayerLobbyConfigDialog;

/**
 * The screen for a lobby.
 */
public class LobbyScreen extends AbstractGGUIScreen {

	@Asset("ui/backgrounds/lobby_screen.jpg")
	private Texture backgroundTexture;

	private final int maxPlayerCount = 7;
	private Label messagesArea, settingsArea;
	private Table[] playerSlots;
	private ImageTextButton readyUpLobbyButton;
	private ScrollPane messagesPane;
	private OffsettableTextField chatInputField;

	private boolean gameStarted = false;

	public LobbyScreen(ProjektGGApplication application) {
		super(application);
	}

	@Override
	protected void create() {
		super.create();
		setImage(backgroundTexture);

		PlayerLobbyConfigDialog playerConfigDialog = new PlayerLobbyConfigDialog(
				application, skin);

		ImageTextButton playerSettingsButton = new ImageTextButton(
				Lang.get("screen.lobby.configure"), skin);
		playerSettingsButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						playerConfigDialog.initUIValues(
								application.getClient().getLobbyPlayers(),
								application.getClient().getLocalLobbyPlayer());
						playerConfigDialog.show(stage);
					}
				});

		ImageTextButton leaveButton = new ImageTextButton(
				Lang.get("screen.lobby.disconnect"), skin);
		leaveButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						Log.info("Client", "Disconnecting from Lobby");
						final GameClient client = application.getClient();
						final GameServer server = application.getServer();
						application.setClient(null);
						application.setServer(null);

						ThreadHandler.getInstance().executeRunnable(() -> {
							client.disconnect();
							Log.info("Client", "Client disconnected");
							if (server != null) {
								server.stop();
							}
							Log.info("Server", "Server stopped");
						});

						application.getScreenManager()
								.pushScreen("server_browser", null);
					}
				});

		readyUpLobbyButton = new ImageTextButton(Lang.get("screen.lobby.ready"),
				skin);
		readyUpLobbyButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						// TODO warum nicht readyUp(); ?
						application.getClient().getLocalLobbyPlayer()
								.toggleReady();
						application.getClient().getActionHandler()
								.changeLocalPlayer(application.getClient()
										.getLocalLobbyPlayer());

						updateLobbyUI();
					}
				});

		settingsArea = new Label("", skin);
		settingsArea.setAlignment(Align.topLeft);
		settingsArea.setWrap(true);

		Table playerTable = new Table();
		Table buttonTable = new Table();
		Table chatTable = new Table();

		buttonTable.add(playerSettingsButton).bottom().padBottom(18).row();
		buttonTable.add(readyUpLobbyButton).padBottom(18).row();
		buttonTable.add(leaveButton).padBottom(50);

		Table chatInputTable = new Table();
		ImageTextButton sendButton = new ImageTextButton(
				Lang.get("screen.lobby.send"), skin);
		chatInputField = new OffsettableTextField("", skin, "large", 8);
		chatInputField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char key) {
				if (!textField.getText().isEmpty() && key == '\n') { // Enter
					application.getSoundManager()
							.playSoundEffect("button_click");

					application.getClient().getActionHandler()
							.sendChatmessage(chatInputField.getText());
					application.getClient().getChatMessages()
							.add(new ChatMessage(
									application.getClient()
											.getLocalLobbyPlayer(),
									chatInputField.getText()));
					setUIValues();
					chatInputField.setText("");
				}
			}
		});
		sendButton.addListener(
				new ButtonClickListener(application.getSoundManager()) {
					@Override
					protected void onClick() {
						application.getClient().getActionHandler()
								.sendChatmessage(chatInputField.getText());
						application.getClient().getChatMessages()
								.add(new ChatMessage(
										application.getClient()
												.getLocalLobbyPlayer(),
										chatInputField.getText()));
						setUIValues();
						chatInputField.setText("");
					}

					@Override
					protected boolean arePreconditionsMet() {
						return !chatInputField.getText().isEmpty();
					}
				});

		messagesArea = new Label("", skin, "text");
		messagesArea.setWidth(425);
		messagesArea.setWrap(true);

		Table messagesTable = new Table();
		messagesTable.add(messagesArea).padLeft(10).left().top().expand();

		messagesPane = new ScrollPane(messagesTable, skin, "with-background");
		messagesPane.setForceScroll(false, true);

		chatInputTable.add(chatInputField).left().width(325).padRight(15);
		chatInputTable.add(sendButton);

		chatTable.add(messagesPane).height(135).width(465).top().row();
		chatTable.add(chatInputTable).left().padTop(10).width(465).bottom();

		playerSlots = new Table[maxPlayerCount];
		for (int i = 0; i < playerSlots.length; i++) {
			playerSlots[i] = new Table();
			playerTable.add(playerSlots[i]).height(29).width(465).row();
		}

		Table mTable = new Table();
		mTable.setWidth(615);
		mTable.setHeight(475);
		mTable.setBackground(skin.getDrawable("parchment1"));
		mTable.add(playerTable).width(465).height(185).padBottom(15);
		mTable.add(settingsArea).width(155).height(185).row();
		mTable.add(chatTable).height(185).bottom();
		mTable.add(buttonTable).height(185);

		mainTable.add(mTable);
	}

	@Override
	protected void setUIValues() {
		GameSessionSetup sessionSetup = application.getClient().getLobbyData()
				.getSessionSetup();
		gameStarted = false;

		if (application.isHost()) {
			readyUpLobbyButton.setText(Lang.get("screen.lobby.start_game"));
		} else {
			readyUpLobbyButton.setDisabled(false);
			readyUpLobbyButton.setTouchable(Touchable.enabled);
		}

		settingsArea.setText(Lang.get("screen.lobby.map", sessionSetup.getMap())
				+ " \n" + Lang.get("screen.lobby.difficulty",
						sessionSetup.getDifficulty()));
		chatInputField.setText("");

		updateLobbyUI();
	}

	/**
	 * Updates the lobby ui after player changes.
	 */
	private void updateLobbyUI() {
		Object[] playersArray = application.getClient().getLobbyPlayers()
				.values().toArray();

		for (int i = 0; i < playerSlots.length; i++) {
			updatePlayerSlot(playerSlots[i],
					(playersArray.length >= (i + 1)
							? (LobbyPlayer) playersArray[i]
							: null));
		}

		messagesArea.setText("");
		for (ChatMessage c : application.getClient().getChatMessages()) {
			addChatMessageToUI(c);
		}

		if (application.isHost()) {
			if (PlayerUtils.areAllPlayersReadyExcept(
					application.getClient().getLobbyPlayers().values(),
					application.getClient().getLocalLobbyPlayer())) {
				readyUpLobbyButton.setDisabled(false);
				readyUpLobbyButton.setTouchable(Touchable.enabled);
			} else {
				readyUpLobbyButton.setDisabled(true);
				readyUpLobbyButton.setTouchable(Touchable.disabled);
			}
		} else {
			if (application.getClient().getLocalLobbyPlayer().isReady()) {
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
			t.add().width(33);
			t.add(new Label("[#D3D3D3FF]" + Lang.get("screen.lobby.free_slot"),
					skin)).width(350);
			t.add().width(47);
		} else {
			// Icon
			t.add(new Image(
					skin.getDrawable(p.getIcon().getIconDrawableName())))
					.padRight(11);
			// Name
			t.add(new Label(Lang.get(p).replace(" ", "  "), // Use two spaces to
															// improve
															// readability
					skin)).width(350);
			// Ready
			t.add(new Image(p.isReady() ? skin.getDrawable("icon_on")
					: skin.getDrawable("icon_off"))).padRight(9);
			// TODO Kick
			if (application.isHost()
					&& p != application.getClient().getLocalLobbyPlayer()) {
				ImageButton kickButton = new ImageButton(skin, "kick");
				t.add(kickButton);
			} else
				t.add().width(27);
		}

		return t;
	}

	private void addChatMessageToUI(ChatMessage message) {
		messagesArea.setText(messagesArea.getText()
				+ (message.isSystemMessage() ? "[#EFE22DFF]"
						: ("[#" + message.getSender().getIcon().getColor() + "]"
								+ Lang.get(message.getSender()) + ": []"))
				+ message.getMessage() + (message.isSystemMessage() ? "[]" : "")
				+ " \n");
		messagesPane.layout();
		messagesPane.scrollTo(0, 0, 0, 0);
	}

	/**
	 * Starts the game if all players are ready.
	 */
	private void startGameIfReady() {
		if (PlayerUtils.areAllPlayersReady(
				application.getClient().getLobbyPlayers().values())
				&& !gameStarted) {
			gameStarted = true;
			addChatMessageToUI(
					new ChatMessage(Lang.get("screen.lobby.game_starting")));
			application.getInputMultiplexer()
					.removeProcessors(new Array<>(getInputProcessors()));

			// Set up the game on the client side
			application.getClient().initGameSession();

			Log.info("Client", "Loading game stuff...");
			application.getScreenManager().pushScreen("game_loading",
					"blendingTransition");
		} else {
			gameStarted = false;
		}
	}

	@Subscribe
	public void onUIRefreshEvent(UIRefreshEvent event) {
		updateLobbyUI();
	}

}
