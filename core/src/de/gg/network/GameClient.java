package de.gg.network;

import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import de.gg.core.ProjektGG;
import de.gg.event.ConnectionEstablishedEvent;
import de.gg.event.ConnectionFailedEvent;
import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerChangedEvent;
import de.gg.event.PlayerConnectedEvent;
import de.gg.event.PlayerDisconnectedEvent;
import de.gg.event.RoundEndEvent;
import de.gg.game.AuthoritativeSession;
import de.gg.game.SlaveSession;
import de.gg.game.data.GameSessionSetup;
import de.gg.game.entity.City;
import de.gg.network.message.ChatMessageSentMessage;
import de.gg.network.message.GameSetupMessage;
import de.gg.network.message.PlayerChangedMessage;
import de.gg.network.message.PlayerJoinedMessage;
import de.gg.network.message.PlayerLeftMessage;
import de.gg.network.message.ServerFullMessage;
import de.gg.network.message.ServerRejectionMessage;
import de.gg.network.rmi.AuthoritativeResultListener;
import de.gg.network.rmi.ClientActionHandler;
import de.gg.network.rmi.SlaveActionListener;
import de.gg.util.Log;

/**
 * This class takes care of handling the networking part for the client. It
 * holds an instance of the used kryonet {@linkplain #client client}, the
 * {@linkplain #session client's game simulation} and the
 * {@linkplain #actionHandler action handler} used for relaying all user actions
 * to the server.
 * <p>
 * Following are the methods relevant to using the client:
 * <ul>
 * <li>{@link #setUpConnectionAsClient(String, int)}/{@link #setUpConnectionAsHost(int, String, GameSessionSetup)}:
 * Initializes the network handler</li>
 * <li>{@link #establishRMIConnection(AuthoritativeResultListener)}: Establishes
 * the client's rmi connection; has to get called after the network handler was
 * {@linkplain #setUpConnectionAsClient(String, int) initialized}</li>
 * <li>{@link #setupGameOnServer()}: Initializes the
 * {@linkplain AuthoritativeSession game session} on the server</li>
 * <li>{@link #updateServer()()}: Has to get called continually to update the
 * {@linkplain AuthoritativeSession game session} on the server</li>
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
	/**
	 * The version of the game.
	 */
	private String gameVersion;

	private SlaveSession session;

	/**
	 * Creates a game client and connects the client to the server. After it is
	 * finished a {@link ConnectionEstablishedEvent} is posted on the
	 * {@linkplain ProjektGG#getEventBus() event bus}.
	 * 
	 * @param ip
	 *            The server's ip.
	 * @param port
	 *            The server's port.
	 */
	public GameClient(EventBus eventBus, String gameVersion, String ip,
			int port) {
		Preconditions.checkNotNull(eventBus, "eventBus cannot be null.");
		Preconditions.checkNotNull(eventBus, "gameVersion cannot be null.");

		this.eventBus = eventBus;
		this.gameVersion = gameVersion;

		client = new Client();
		client.start();

		NetworkRegisterer.registerClasses(client.getKryo());

		TypeListener listener = new TypeListener();
		// CLIENT CONNECTION
		// Game setup (on client connect)
		listener.addTypeHandler(GameSetupMessage.class, (con, msg) -> {
			if (gameVersion.equals(msg.getServerVersion())) { // right server
																// version
				eventBus.post(new ConnectionEstablishedEvent(msg.getPlayers(),
						msg.getId(), msg.getSettings()));
				localClientId = msg.getId();
				Log.info("Client", "Verbindung hergestellt. Netzwerk ID: %d",
						localClientId);
			} else { // wrong server version
				eventBus.post(
						new ConnectionFailedEvent(new ServerRejectionMessage() {
							@Override
							public String getMessage() {
								return "Falsche Server-Version: "
										+ msg.getServerVersion();
							}
						}));
				con.close();
				Log.info("Client",
						"Fehler beim verbinden: Falsche Server-Version (%s)",
						msg.getServerVersion());
			}
		});
		// Server full
		listener.addTypeHandler(ServerFullMessage.class, (con, msg) -> {
			eventBus.post(new ConnectionFailedEvent(msg));
			con.close();
			Log.info("Client", "Fehler beim verbinden: %s", msg.getMessage());
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

		final Thread connectingThread = new Thread(new Runnable() {
			public void run() {
				try {
					client.connect(6000, ip, port);
					Log.info("Client", "Lobby beigetreten");
					// Das Event hierfür wird beim Empfangen des Game Setups
					// gepostet
				} catch (IOException e) {
					Log.error("Client", "Fehler beim Verbinden: ", e);
					eventBus.post(new ConnectionFailedEvent(e));
				}
			}
		});
		connectingThread.start();
	}

	public void disconnect() {
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
	 * Sends an object to the server.
	 * 
	 * @param obj
	 *            The object.
	 */
	public void sendObject(Object obj) {
		client.sendTCP(obj);
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
		sendObject(new PlayerChangedMessage(localClientId, player));
	}

	/**
	 * Sends a chat message to the server.
	 * 
	 * @param message
	 */
	public void sendChatMessage(String message) {
		sendObject(new ChatMessageSentMessage(localClientId, message));
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
	 */
	public void establishRMIConnection(HashMap<Short, LobbyPlayer> players,
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
	}

	/**
	 * Sets up the session. The game assets have to get loaded first.
	 */
	public void setupGameSession() {
		session.setupGame();
	}

	public City getCity() {
		return session.getCity();
	}

}
