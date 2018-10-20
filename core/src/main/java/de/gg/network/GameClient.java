package de.gg.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.gg.core.ProjektGG;
import de.gg.events.ConnectionEstablishedEvent;
import de.gg.events.ConnectionFailedEvent;
import de.gg.events.DisconnectionEvent;
import de.gg.events.NewChatMessagEvent;
import de.gg.events.NewNotificationEvent;
import de.gg.events.PlayerChangedEvent;
import de.gg.events.PlayerConnectedEvent;
import de.gg.events.PlayerDisconnectedEvent;
import de.gg.events.RoundEndEvent;
import de.gg.game.GameClock;
import de.gg.game.GameSession;
import de.gg.game.GameSessionSetup;
import de.gg.game.SlaveSession;
import de.gg.game.ai.CharacterBehaviour;
import de.gg.game.data.NotificationData;
import de.gg.game.entities.Player;
import de.gg.game.world.World;
import de.gg.network.messages.ChatMessageSentMessage;
import de.gg.network.messages.ClientSetupMessage;
import de.gg.network.messages.GameSetupMessage;
import de.gg.network.messages.PlayerChangedMessage;
import de.gg.network.messages.PlayerJoinedMessage;
import de.gg.network.messages.PlayerLeftMessage;
import de.gg.network.messages.ServerAcceptanceMessage;
import de.gg.network.messages.ServerRejectionMessage;
import de.gg.network.rmi.ClientActionHandler;
import de.gg.network.rmi.SlaveActionListener;
import de.gg.utils.MachineIdentificationUtils;
import de.gg.utils.log.Log;

/**
 * This class takes care of handling the networking part for the client. It
 * holds an instance of the used kryonet {@linkplain #client client}, the
 * {@linkplain #session client's game simulation} and the
 * {@linkplain #actionHandler action handler} used for relaying all user actions
 * to the server.
 * <p>
 * Following are the relevant states the client can be in:
 * <ul>
 * <li>{@link #connect(String, String, int)}: Connects the client to the server
 * asynchronously. After it is finished a {@link ConnectionEstablishedEvent} is
 * posted to the event bus.</li>
 * <li>{@link #establishRMIConnection(HashMap, GameSessionSetup)}: Establishes
 * the client's RMI connection. Has to get called after the client is
 * connected.</li>
 * <li>{@link #initGameSession()}: Initializes the client's game session. Has to
 * get called after the client is connected and before the session is
 * {@linkplain GameSession#update() updated}.</li>
 * <li>{@link #update()}: Has to get called continually to update the client's
 * {@linkplain SlaveSession game session}.</li>
 * <li>{@link #disconnect()}: Disconnects the client synchronously.</li>
 * </ul>
 */
public class GameClient {

	private EventBus eventBus;
	private Client client;
	/**
	 * The network ID of the local player.
	 */
	private short localClientId;
	private ClientActionHandler actionHandler;

	private float timeSinceLastPingUpdate = 0;
	/**
	 * The time after which the ping gets updated (in seconds).
	 */
	private final int PING_UPDATE_INTERVAL = 10;
	/**
	 * The last measured ping.
	 */
	private int ping;

	private List<NotificationData> notifications = new ArrayList<>();

	private SlaveSession session;

	/**
	 * Creates a game client.
	 *
	 * @param eventBus
	 *            the event bus used by the client.
	 */
	public GameClient(EventBus eventBus) {
		Preconditions.checkNotNull(eventBus, "eventBus cannot be null.");

		this.eventBus = eventBus;
		this.eventBus.register(this);
	}

	/**
	 * Tries to connect the client to the server asynchronously. After it is
	 * finished either a {@link ConnectionEstablishedEvent} or a
	 * {@link ConnectionFailedEvent} is posted on the
	 * {@linkplain ProjektGG#getEventBus() event bus}.
	 *
	 * @param gameVersion
	 *            the client's game version.
	 * @param ip
	 *            The server's ip address.
	 * @param port
	 *            The server's port.
	 */
	public void connect(String gameVersion, String ip, int port) {
		Preconditions.checkNotNull(gameVersion, "gameVersion cannot be null.");
		Preconditions.checkNotNull(ip, "ip cannot be null.");

		client = new Client();
		client.start();

		Log.info("Client", "--- Neuem Spiel wird beigetreten ---");

		NetworkRegisterer.registerClasses(client.getKryo());

		TypeListener listener = new TypeListener();
		// CLIENT CONNECTION
		// On Server acception
		listener.addTypeHandler(ServerAcceptanceMessage.class, (con, msg) -> {
			if (gameVersion.equals(msg.getServerVersion())) { // right server
																// version
				// Send client setup & wait for game setup
				client.sendTCP(new ClientSetupMessage(
						MachineIdentificationUtils.getHostname()));
			} else { // wrong server version
				Log.info("Client",
						"Fehler beim Verbinden: Falsche Server-Version (%s)",
						msg.getServerVersion());

				eventBus.post(new ConnectionFailedEvent(
						new ServerRejectionMessage("Falsche Server-Version: "
								+ msg.getServerVersion())));
				con.close();
			}

		});
		// Game setup
		listener.addTypeHandler(GameSetupMessage.class, (con, msg) -> {
			Log.info("Client", "Lobby beigetreten. Spieler- & Netzwerk-ID: %d",
					msg.getId());
			localClientId = msg.getId();

			eventBus.post(new ConnectionEstablishedEvent(msg.getPlayers(),
					msg.getId(), msg.getSessionSetup()));
		});
		// Server full
		listener.addTypeHandler(ServerRejectionMessage.class, (con, msg) -> {
			Log.info("Client", "Fehler beim Verbinden: %s", msg.getMessage());
			eventBus.post(new ConnectionFailedEvent(msg));
		});

		// PLAYER CHANGES
		// Player joined
		listener.addTypeHandler(PlayerJoinedMessage.class, (con, msg) -> {
			eventBus.post(
					new PlayerConnectedEvent(msg.getId(), msg.getPlayer()));
		});
		// Player left
		listener.addTypeHandler(PlayerLeftMessage.class, (con, msg) -> {
			eventBus.post(new PlayerDisconnectedEvent(msg.getId()));
		});
		// Player changed
		listener.addTypeHandler(PlayerChangedMessage.class, (con, msg) -> {
			eventBus.post(new PlayerChangedEvent(msg.getId(), msg.getPlayer()));
		});

		// New chat message
		listener.addTypeHandler(ChatMessageSentMessage.class, (con, msg) -> {
			eventBus.post(new NewChatMessagEvent(msg.getSenderId(),
					msg.getMessage()));
		});
		// Ping
		listener.addTypeHandler(Ping.class, (con, msg) -> {
			if (msg.isReply) {
				this.ping = con.getReturnTripTime();
				Log.info("Client", "Ping: %d", ping);
			}
		});
		client.addListener(listener);
		client.addListener(new Listener() {
			@Override
			public void disconnected(Connection connection) {
				eventBus.post(new DisconnectionEvent());
			}
		});

		final Thread connectingThread = new Thread(() -> {
			try {
				client.connect(6000, ip, port);
				Log.info("Client", "Verbindung zum Server hergestellt");
				// Das Event für einen erfolgreichen Verbingungsvorgang wird
				// beim Empfangen des Game Setups gepostet
			} catch (IOException e) {
				Log.error("Client", "Fehler beim Verbinden: ", e);
				eventBus.post(new ConnectionFailedEvent(e));
			}
		});
		connectingThread.start();
	}

	public void disconnect() {
		eventBus.unregister(this);
		client.close();
	}

	/**
	 * @return the action handler used to relay the client's action to the
	 *         server.
	 */
	public ClientActionHandler getActionHandler() {
		return actionHandler;
	}

	/**
	 * Updates the ping.
	 *
	 * @param delta
	 *            The time delta in seconds.
	 * @see #PING_UPDATE_INTERVAL
	 */
	public synchronized void updatePing(float delta) {
		timeSinceLastPingUpdate += delta;

		if (timeSinceLastPingUpdate >= PING_UPDATE_INTERVAL) {
			timeSinceLastPingUpdate -= PING_UPDATE_INTERVAL;
			client.updateReturnTripTime();
		}
	}

	/**
	 * Updates the client and its session.
	 */
	public void update() {
		if (session.update())
			eventBus.post(new RoundEndEvent());
	}

	/**
	 * Updates the player on the server.
	 */
	public void onLocalPlayerChange(LobbyPlayer player) {
		client.sendTCP(new PlayerChangedMessage(localClientId, player));
	}

	/**
	 * Sends a chat message to the server.
	 *
	 * @param message
	 *            The actual chat message.
	 */
	public void sendChatMessage(String message) {
		client.sendTCP(new ChatMessageSentMessage(localClientId, message));
	}

	@Subscribe
	public void onNotificationCreation(NewNotificationEvent ev) {
		notifications.add(ev.getData());
	}

	/**
	 * @return A list of all notifications for this client.
	 * @see NewNotificationEvent The event with which new notifications get
	 *      added.
	 */
	public List<NotificationData> getNotifications() {
		return notifications;
	}

	/**
	 * Returns the last measured ping. Only gets updated if
	 * {@link #updatePing(float)} is called regularly.
	 *
	 * @return The last measured ping.
	 */
	public int getPing() {
		return ping;
	}

	/**
	 * Establishes the RMI connection to the server.
	 *
	 * @param players
	 * @param sessionSetup
	 */
	public synchronized void establishRMIConnection(
			HashMap<Short, LobbyPlayer> players,
			GameSessionSetup sessionSetup) {
		session = new SlaveSession(eventBus, sessionSetup, players,
				localClientId);

		ObjectSpace.registerClasses(client.getKryo());
		ObjectSpace objectSpace = new ObjectSpace();
		objectSpace.register(localClientId, session);
		objectSpace.addConnection(client);

		SlaveActionListener actionListener = ObjectSpace.getRemoteObject(client,
				254, SlaveActionListener.class);

		if (actionListener == null)
			Log.error("Client", "Der actionListener des Spielers %d ist null",
					localClientId);

		this.actionHandler = new ClientActionHandler(localClientId,
				actionListener);

		Log.info("Client", "RMI-Netzwerkverbindung zum Server eingerichtet");
	}

	/**
	 * Initializes the session. The {@linkplain de.gg.game.types game assets}
	 * have to get loaded first.
	 */
	public void initGameSession() {
		session.init(null);
	}

	public World getWorld() {
		return session.getWorld();
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

	/**
	 * @return the local player.
	 */
	public Player getLocalPlayer() {
		return session.getWorld().getPlayers().get(localClientId);
	}

	/**
	 * @return the current round.
	 */
	public int getGameRound() {
		return session.getRound();
	}

	/**
	 * @return the game's clock.
	 */
	public GameClock getGameClock() {
		return session.getClock();
	}

}
