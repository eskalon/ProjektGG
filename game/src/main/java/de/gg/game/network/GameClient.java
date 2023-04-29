package de.gg.game.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.gg.engine.network.BaseGameClient;
import de.gg.game.ai.CharacterBehaviour;
import de.gg.game.events.ConnectionLostEvent;
import de.gg.game.events.LobbyDataReceivedEvent;
import de.gg.game.events.NotificationCreationEvent;
import de.gg.game.events.RoundEndEvent;
import de.gg.game.events.UIRefreshEvent;
import de.gg.game.model.entities.Character;
import de.gg.game.model.entities.Player;
import de.gg.game.network.rmi.ClientsideActionHandler;
import de.gg.game.network.rmi.SlaveActionListener;
import de.gg.game.session.GameSession;
import de.gg.game.session.SlaveSession;
import de.gg.game.ui.data.ChatMessage;
import de.gg.game.ui.data.NotificationData;

/**
 * This class takes care of handling the networking part for the client. It
 * holds an instance of the used kryonet {@linkplain #client client}, the
 * {@linkplain #session client's game simulation} and the
 * {@linkplain #actionHandler action handler} used for relaying all user actions
 * to the server.
 * <p>
 * Following are all methods concerning the state of the client:
 * <ul>
 * <li>
 * {@link #connect(de.gg.engine.network.IClientConnectCallback, String, String, int)}:
 * Connects the client to the server asynchronously. After it is finished the
 * given listener is called. After that the client requests the lobby data. When
 * he receives it a {@link LobbyDataReceivedEvent} is posted.</li>
 * <li>{@link #initGameSession()}: Initializes the client's game session. Has to
 * get called after the client is connected and before the session is
 * {@linkplain GameSession#update() updated}.</li>
 * <li>{@link #update()}: Has to get called continually to update the client's
 * {@linkplain SlaveSession game session}.</li>
 * <li>{@link #disconnect()}: Disconnects the client synchronously.</li>
 * </ul>
 */
public class GameClient extends BaseGameClient {

	private static final Logger LOG = LoggerService.getLogger(GameClient.class);

	private EventBus eventBus;

	private ClientsideActionHandler actionHandler;
	private ClientsideResultListener resultListener;
	private ObjectSpace objectSpace;

	private boolean inLobby = true;
	private boolean orderlyDisconnect = false;

	/* --- The networked data --- */
	private List<NotificationData> notifications = new ArrayList<>();
	private SlaveSession session;
	List<ChatMessage> chatMessages = new ArrayList<>();

	HashMap<Short, LobbyPlayer> lobbyPlayers;
	LobbyData lobbyData;

	public GameClient(EventBus eventBus) {
		this.eventBus = eventBus;
		this.eventBus.register(this);

		this.resultListener = new ClientsideResultListener(eventBus, this);
	}

	@Override
	protected void onCreation() {
		NetworkRegisterer.registerClasses(client.getKryo());
	}

	@Override
	protected void onDisconnection() {
		if (!orderlyDisconnect) {
			LOG.info("[CLIENT] Connection to server lost");
			eventBus.post(new ConnectionLostEvent());
		}
	}

	@Override
	protected void onSuccessfulHandshake() {
		// Establish RMI
		ObjectSpace.registerClasses(client.getKryo());
		objectSpace = new ObjectSpace();
		objectSpace.register(localClientId, resultListener);
		objectSpace.addConnection(client);

		SlaveActionListener actionListener = ObjectSpace.getRemoteObject(client,
				254, SlaveActionListener.class);

		if (actionListener == null)
			LOG.error("[CLIENT] actionListener of player %d is null",
					localClientId);

		this.actionHandler = new ClientsideActionHandler(localClientId,
				actionListener);

		actionHandler.requestGameData();

		LOG.info("[CLIENT] RMI connection to server established");
	}

	@Override
	public void disconnect() {
		orderlyDisconnect = true;
		eventBus.unregister(this);
		objectSpace.close();
		super.disconnect();
	}

	/**
	 * Initializes the session. The {@linkplain de.gg.game.model.types game
	 * assets} have to get loaded first.
	 *
	 * @param sessionSetup
	 * @param players
	 * @param savedGame
	 */
	public void initGameSession() {
		inLobby = false;
		session = new SlaveSession(eventBus, lobbyData.getSessionSetup(),
				localClientId);
		session.init(lobbyPlayers, lobbyData.getSavedGame());

		resultListener.setSession(session);

		LOG.info("[CLIENT] Match initialized");
	}

	/**
	 * Updates the client and its session.
	 */
	public void update() {
		if (session.update())
			eventBus.post(new RoundEndEvent());
	}

	/**
	 * @return the action handler used to relay the client's action to the
	 *         server.
	 */
	public ClientsideActionHandler getActionHandler() {
		return actionHandler;
	}

	@Subscribe
	public void onNotificationCreation(NotificationCreationEvent ev) {
		notifications.add(ev.getData());
		eventBus.post(new UIRefreshEvent());
	}

	public boolean isInLobby() {
		return inLobby;
	}

	public LobbyData getLobbyData() {
		return lobbyData;
	}

	/**
	 * @return A list of all notifications for this client.
	 * @see NotificationCreationEvent The event to post to create a new
	 *      notification.
	 */
	public List<NotificationData> getNotifications() {
		return notifications;
	}

	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}

	public HashMap<Short, LobbyPlayer> getLobbyPlayers() {
		return lobbyPlayers;
	}

	public LobbyPlayer getLocalLobbyPlayer() {
		return lobbyPlayers.get(localClientId);
	}

	/**
	 * @param otherCharacterId
	 * @return the opinion another character has about the player.
	 */
	public int getOpinionOfOtherCharacter(short otherCharacterId) {
		return CharacterBehaviour.getOpinionOfAnotherCharacter(
				getLocalPlayer().getCurrentlyPlayedCharacterId(),
				otherCharacterId, session);
	}

	public Player getLocalPlayer() {
		return session.getWorld().getPlayers().get(localClientId);
	}

	public Character getLocalPlayerCharacter() {
		return session.getWorld().getPlayers().get(localClientId)
				.getCurrentlyPlayedCharacter(session.getWorld());
	}

	/**
	 * @return the current game session.
	 */
	public GameSession getSession() {
		return session;
	}

}
