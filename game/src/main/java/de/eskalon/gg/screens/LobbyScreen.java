package de.eskalon.gg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntMap.Entry;

import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.core.EskalonApplicationStarter;
import de.eskalon.commons.event.EventBus;
import de.eskalon.commons.event.Subscribe;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.net.data.ChatMessage;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.screens.AbstractEskalonUIScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.gg.core.ProjektGGApplicationContext;
import de.eskalon.gg.events.AllPlayersReadyEvent;
import de.eskalon.gg.events.ChatMessageEvent;
import de.eskalon.gg.events.ConnectionLostEvent;
import de.eskalon.gg.events.LobbyDataChangedEvent;
import de.eskalon.gg.graphics.ui.actors.OffsettableTextField;
import de.eskalon.gg.graphics.ui.actors.dialogs.PlayerLobbyConfigDialog;
import de.eskalon.gg.input.ButtonClickListener;
import de.eskalon.gg.misc.ObjectCopyUtils;
import de.eskalon.gg.misc.PlayerUtils;
import de.eskalon.gg.net.GameClient;
import de.eskalon.gg.net.GameServer;
import de.eskalon.gg.net.PlayerData;
import de.eskalon.gg.screens.game.GameLoadingScreen;
import de.eskalon.gg.simulation.GameHandler;
import de.eskalon.gg.simulation.GameSetup;
import de.eskalon.gg.simulation.GameState;

/**
 * The screen for a lobby.
 */
public class LobbyScreen extends AbstractEskalonUIScreen {

	private static final Logger LOG = LoggerService
			.getLogger(LobbyScreen.class);

	private @Inject Skin skin;
	private @Inject EskalonScreenManager screenManager;
	private @Inject ProjektGGApplicationContext appContext;
	private @Inject ISoundManager soundManager;
	private @Inject EventBus eventBus;

	@Asset("ui/backgrounds/lobby_screen.jpg")
	private @Inject Texture backgroundTexture;

	private final int maxPlayerCount = 7;
	private Label messagesArea, settingsArea;
	private Table[] playerSlots;
	private ImageTextButton readyUpLobbyButton;
	private ScrollPane messagesPane;
	private OffsettableTextField chatInputField;
	private ImageTextButton playerSettingsButton;

	private LobbyData<GameSetup, GameState, PlayerData> lobbyDataCopy;

	public void setLobbyData(
			LobbyData<GameSetup, GameState, PlayerData> lobbyData) {

		this.lobbyDataCopy = ObjectCopyUtils.instance().copy(lobbyData);
	}

	@Override
	public void show() {
		super.show();

		setImage(backgroundTexture);

		PlayerLobbyConfigDialog playerConfigDialog = new PlayerLobbyConfigDialog(
				skin, soundManager, eventBus, appContext.getClient());

		playerSettingsButton = new ImageTextButton(
				Lang.get("screen.lobby.configure"), skin);
		playerSettingsButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				playerConfigDialog.initUIValues(lobbyDataCopy.getPlayers(),
						appContext.getClient().getLocalLobbyPlayer());
				playerConfigDialog.show(stage);
			}
		});

		ImageTextButton leaveButton = new ImageTextButton(
				Lang.get("screen.lobby.disconnect"), skin);
		leaveButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				LOG.info("[CLIENT] Disconnecting from Lobby");
				final GameClient client = appContext.getClient();
				final GameServer server = appContext.getServer();
				appContext.setClient(null);
				appContext.setServer(null);

				ThreadHandler.instance().executeRunnable(() -> {
					client.disconnect();
					LOG.info("[CLIENT] Client disconnected");
					if (server != null) {
						server.stop();
					}
					LOG.info("[SERVER] Server stopped");
				});

				screenManager.pushScreen(ServerBrowserScreen.class);
			}
		});

		readyUpLobbyButton = new ImageTextButton(Lang.get("screen.lobby.ready"),
				skin);
		readyUpLobbyButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				appContext.getClient().getLocalLobbyPlayer().toggleReady();
				appContext.getClient().changeLocalPlayerData(
						appContext.getClient().getLocalLobbyPlayer());

				if (appContext.getClient().getLocalLobbyPlayer().isReady()) {
					playerSettingsButton.setDisabled(true);
					playerSettingsButton.setTouchable(Touchable.disabled);
				} else {
					playerSettingsButton.setDisabled(false);
					playerSettingsButton.setTouchable(Touchable.enabled);
				}

				updateLobbyUI();
			}
		});

		if (appContext.isHost()) {
			readyUpLobbyButton.setText(Lang.get("screen.lobby.start_game"));
		} else {
			readyUpLobbyButton.setDisabled(false);
			readyUpLobbyButton.setTouchable(Touchable.enabled);
		}

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
					soundManager.playSoundEffect("button_click");

					appContext.getClient()
							.sendChatMessage(chatInputField.getText());
					updateLobbyUI();
					chatInputField.setText("");
				}
			}
		});
		sendButton.addListener(new ButtonClickListener(soundManager) {
			@Override
			protected void onClick() {
				appContext.getClient()
						.sendChatMessage(chatInputField.getText());
				updateLobbyUI();
				chatInputField.setText("");
			}

			@Override
			protected boolean arePreconditionsMet() {
				return !chatInputField.getText().isEmpty();
			}
		});

		messagesArea = new Label("\n\n\n\n\n", skin, "text");
		messagesArea.setWidth(425);
		messagesArea.setWrap(true);

		Table messagesTable = new Table();
		messagesTable.add(messagesArea).padLeft(10).padBottom(1).left().top()
				.expand().fill();

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

		updateLobbyUI();
	}

	/**
	 * Updates the lobby ui after player changes.
	 */
	private void updateLobbyUI() {
		settingsArea.setText(Lang.get("screen.lobby.map",
				lobbyDataCopy.getSessionSetup().getMap()) + " \n"
				+ Lang.get("screen.lobby.difficulty",
						lobbyDataCopy.getSessionSetup().getDifficulty()));

		int i = 0;

		for (Entry<PlayerData> e : lobbyDataCopy.getPlayers().entries()) {
			updatePlayerSlot(playerSlots[i], e.value,
					e.key == appContext.getClient().getLocalNetworkID());
			i++;
		}

		// Fill in the empty slots
		for (int j = 0; i < playerSlots.length; i++) {
			updatePlayerSlot(playerSlots[i], null, false);
		}

		messagesArea.setText("\n\n\n\n\n");
		for (ChatMessage c : appContext.getClient().getChatMessages()) {
			addChatMessageToUI(c);
		}

		if (appContext.isHost()) {
			if (PlayerUtils.areAllPlayersReadyExcept(
					lobbyDataCopy.getPlayers().values(),
					appContext.getClient().getLocalLobbyPlayer())) {
				readyUpLobbyButton.setDisabled(false);
				readyUpLobbyButton.setTouchable(Touchable.enabled);
			} else {
				readyUpLobbyButton.setDisabled(true);
				readyUpLobbyButton.setTouchable(Touchable.disabled);
			}
		} else {
			if (appContext.getClient().getLocalLobbyPlayer().isReady()) {
				readyUpLobbyButton.setText(Lang.get("screen.lobby.not_ready"));
			} else {
				readyUpLobbyButton.setText(Lang.get("screen.lobby.ready"));
			}
		}
	}

	private Table updatePlayerSlot(Table t, PlayerData p,
			boolean isLocalPlayer) {
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
			// Host / Kick
			if (appContext.isHost()) {
				if (!isLocalPlayer) {
					ImageButton kickButton = new ImageButton(skin, "kick");
					kickButton
							.addListener(new ButtonClickListener(soundManager) {
								@Override
								protected void onClick() {
									// TODO implement kicking
								}
							});
					t.add(kickButton);
				} else {
					t.add().width(27);
				}
			} else {
				// TODO add host icon
				t.add().width(27);
			}
		}

		return t;
	}

	private void addChatMessageToUI(ChatMessage message) {
		messagesArea.setText(messagesArea.getText() + " \n"
				+ (message.isSystemMessage() ? "[#EFE22DFF]"
						: ("[#" + ((PlayerData) message.getSender()).getIcon()
								.getColor() + "]"
								+ Lang.get((PlayerData) message.getSender())
								+ ": []"))
				+ message.getMessage()
				+ (message.isSystemMessage() ? "[]" : ""));
		messagesPane.scrollTo(0, 0, 0, 0);
	}

	@Subscribe
	public void onLobbyDataChangedEvent(LobbyDataChangedEvent ev) {
		this.lobbyDataCopy = ObjectCopyUtils.instance().copy(ev.getNewData());
		updateLobbyUI();
	}

	@Subscribe
	public void onChatMessageEvent(ChatMessageEvent<?> event) {
		updateLobbyUI();
	}

	@Subscribe
	public void onAllPlayersReadyEvent(AllPlayersReadyEvent event) {
		for (PlayerData p : lobbyDataCopy.getPlayers().values()) {
			// The client has already unreadied its players objects, but for the
			// UI we need them to stay ready
			p.setReady(true);
		}

		updateLobbyUI();

		addChatMessageToUI(
				new ChatMessage(Lang.get("screen.lobby.game_starting")));
		((EskalonApplicationStarter) Gdx.app.getApplicationListener())
				.getApplication().getInputMultiplexer().removeProcessor(stage);

		// Set up the game on the client side
		GameHandler handler = EskalonInjector.instance()
				.getInstance(GameHandler.class);
		handler.init(appContext.getClient());
		appContext.setGameHandler(handler);
		appContext.getObjectStorage().put("game_has_just_started", "");

		LOG.info("[CLIENT] Loading game stuff...");
		screenManager.pushScreen(GameLoadingScreen.class, "blendingTransition");
	}

	@Subscribe
	public void onConnectionLostEvent(ConnectionLostEvent ev) {
		appContext.clearGame();

		ServerBrowserScreen screen = EskalonInjector.instance()
				.getInstance(ServerBrowserScreen.class);
		screen.setConnectionLost(true);
		screenManager.pushScreen(screen, null);
	}

}
