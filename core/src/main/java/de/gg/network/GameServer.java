package de.gg.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ConnectionListener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.ServerDiscoveryHandler;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;
import com.google.common.base.Preconditions;

import de.gg.game.AuthoritativeSession;
import de.gg.game.GameSession;
import de.gg.game.GameSessionSetup;
import de.gg.game.SavedGame;
import de.gg.game.entities.Character;
import de.gg.game.entities.Player;
import de.gg.network.messages.ChatMessageSentMessage;
import de.gg.network.messages.ClientSetupMessage;
import de.gg.network.messages.DiscoveryResponsePacket;
import de.gg.network.messages.GameSetupMessage;
import de.gg.network.messages.PlayerChangedMessage;
import de.gg.network.messages.PlayerJoinedMessage;
import de.gg.network.messages.PlayerLeftMessage;
import de.gg.network.messages.ServerAcceptanceMessage;
import de.gg.network.messages.ServerRejectionMessage;
import de.gg.network.rmi.AuthoritativeResultListener;
import de.gg.utils.Log;
import de.gg.utils.PlayerUtils;
import de.gg.utils.PlayerUtils.PlayerStub;

public class GameServer {

	public static final int DEFAULT_PORT = 55678;
	public static final int UDP_DISCOVER_PORT = 54678;
	/**
	 * The network id of the local player. Is always <code>0</code>.
	 */
	public static final short HOST_PLAYER_NETWORK_ID = 0;
	private Server server;
	private Server broadcastServer;
	private GameSessionSetup sessionSetup;
	private AuthoritativeSession session;
	private ServerSetup serverSetup;

	private List<PlayerStub> playerStubs;

	/**
	 * A count of all joined players. Used to generate the player IDs.
	 */
	private short playerIdIterator = 0;
	/**
	 * A hashmap of all players, keyed by their id. After the client
	 * {@linkplain #onClientSetup(Connection, ClientSetupMessage) is setup} the
	 * respective {@linkplain #connections connection} gets assigned a player.
	 */
	private HashMap<Short, LobbyPlayer> players;
	/**
	 * Maps the connections to the player ids.
	 */
	private HashMap<Connection, Short> connections;
	/**
	 * <i>Not</i> <code>null</code> if the server hosts a previously saved game.
	 */
	private SavedGame savedGame;

	/**
	 * Creates a server object with the specified settings.
	 *
	 * @param serverSetup
	 *            The server's settings, especially containing the port.
	 * @param sessionSetup
	 *            The game session's setup.
	 * @param savedGame
	 *            The saved game session to host. Can be <code>null</code>.
	 */
	public GameServer(ServerSetup serverSetup, GameSessionSetup sessionSetup,
			@Nullable SavedGame savedGame, List<PlayerStub> playerStubs) {
		Preconditions.checkNotNull(serverSetup, "server setup cannot be null");
		Preconditions.checkNotNull(sessionSetup,
				"session setup cannot be null");
		Preconditions.checkArgument(
				playerStubs.size() >= serverSetup.getMaxPlayerCount(),
				"there have to be enough player stubs for the max player size");

		this.players = new HashMap<>();
		this.connections = new HashMap<>();

		this.sessionSetup = sessionSetup;
		this.serverSetup = serverSetup;
		this.savedGame = savedGame;
		this.playerStubs = playerStubs;
	}

	/**
	 * Sets up a server asynchronously. After it is finished the callback is
	 * informed.
	 *
	 * @param callback
	 *            the callback that is informed when the server is started.
	 */
	public void start(IHostCallback callback) {
		Preconditions.checkNotNull(callback, "callback cannot be null.");
		this.server = new Server();
		this.server.start();

		NetworkRegisterer.registerClasses(server.getKryo());

		// ON NEW CONNECTION & ON DICONNECTED
		this.server.addListener(new ConnectionListener() {
			@Override
			public void connected(Connection con) {
				onNewConnection(con);
			}

			@Override
			public void disconnected(Connection con) {
				onDisconnect(con);
			}
		});
		TypeListener typeListener = new TypeListener();
		// CLIENT SETUP
		typeListener.addTypeHandler(ClientSetupMessage.class,
				(con, msg) -> onClientSetup(con, msg));
		// CHAT MESSAGE
		typeListener.addTypeHandler(ChatMessageSentMessage.class,
				(con, msg) -> {
					server.sendToAllExceptTCP(con.getID(), msg);
				});
		// PLAYER CHANGED
		typeListener.addTypeHandler(PlayerChangedMessage.class,
				(con, msg) -> onPlayerChange(con, msg));

		this.server.addListener(typeListener);

		Thread t = new Thread(() -> {
			try {
				// Server starten
				server.bind(serverSetup.getPort());
				Log.info("Server", "Server gestartet");

				// Wenn das erfolgreich war, einen Broadcast Server starten
				if (serverSetup.isPublic()) {
					broadcastServer = new Server();
					broadcastServer.start();
					broadcastServer.getKryo()
							.register(DiscoveryResponsePacket.class);
					broadcastServer
							.setDiscoveryHandler(new ServerDiscoveryHandler() {
								@Override
								public boolean onDiscoverHost(
										DatagramChannel datagramChannel,
										InetSocketAddress fromAddress)
										throws IOException {
									DiscoveryResponsePacket packet = new DiscoveryResponsePacket(
											serverSetup.getPort(),
											serverSetup.getGameName(),
											players.size(),
											serverSetup.getMaxPlayerCount());

									ByteBuffer buffer = ByteBuffer
											.allocate(256);
									broadcastServer.getSerializationFactory()
											.newInstance(null)
											.write(buffer, packet);
									buffer.flip();

									datagramChannel.send(buffer, fromAddress);

									return true;
								}
							});

					try {
						broadcastServer.bind(0, UDP_DISCOVER_PORT);
						Log.info("Server", "Broadcast-Server gestartet");
					} catch (IOException e1) {
						Log.error("Server",
								"Der Broadcast-Server konnte nicht gestartet werden: %s",
								e1);
					}
				}
				callback.onHostStarted(null);
			} catch (IOException | IllegalArgumentException e2) {
				callback.onHostStarted(e2);
				Log.error("Server",
						"Der Server konnte nicht gestartet werden: %s", e2);
			}
		});
		t.start();
	}

	public void update() {
		if (session.update()) {
			session.onRoundEnd();
		}
	}

	/**
	 * Initializes the game session. The {@linkplain de.gg.game.types game
	 * assets} have to get loaded first.
	 *
	 * @see GameSession#init(SavedGame)
	 */
	public void initGameSession() {
		session.init(null);
		Log.info("Server", "Spiel gestartet");
	}

	/**
	 * Starts the actual game. Has to get called after the session is
	 * {@linkplain #initGameSession() initialized}.
	 *
	 * @see AuthoritativeResultListener#onServerReady()
	 */
	public void startGame() {
		session.getResultListenerStub().onServerReady();
	}

	/**
	 * Stops the server. Also takes care of saving the game.
	 */
	public void stop() {
		// if (session != null)
		// session.saveGame();
		server.stop();
		if (broadcastServer != null)
			broadcastServer.stop();
	}

	// LISTENER METHDOS
	// ON NEW CONNECTION
	private synchronized void onNewConnection(Connection con) {
		if (players.size() >= serverSetup.getMaxPlayerCount()) { // Match full
			Log.info("Server", "Client mangels Kapazität abgewiesen");

			con.sendTCP(
					new ServerRejectionMessage("Der Server ist bereits voll"));
			con.close();
		} else { // Still free slots
			Log.info("Server", "Client verbunden");

			connections.put(con, playerIdIterator);
			con.sendTCP(new ServerAcceptanceMessage(serverSetup.getVersion()));
			playerIdIterator++;
		}
	}

	// ON DISCONNECT
	private synchronized void onDisconnect(Connection con) {
		Short id = connections.remove(con);

		if (id != null) {
			Log.info("Server", "Client %d hat die Verbindung getrennt", id);

			if (players.containsKey(id)) {
				server.sendToAllExceptTCP(con.getID(),
						new PlayerLeftMessage(id));

				if (session != null)
					session.getResultListeners().remove(id);

				players.remove(id);
			}
		}
	}

	// ON CLIENT SETUP
	private synchronized void onClientSetup(Connection con,
			ClientSetupMessage msg) {
		Short id = connections.get(con);

		Log.info("Server", "Client %d wird als Spieler registriert", id);
		LobbyPlayer p;

		if (savedGame != null) {
			short foundId = -1;
			for (Entry<Short, String> e : savedGame.clientIdentifiers
					.entrySet()) {
				if (e.getValue().equals(msg.getHostname())) {
					foundId = e.getKey();
					break;
				}
			}

			if (foundId == -1) {
				Log.info("Server",
						"Kick: Client ist kein Teil dieser geladenen Partie");
				con.sendTCP(new ServerRejectionMessage(
						"Der Spieler ist kein Teil dieser geladenen Partie"));
				con.close();
				return;
			} else {
				if ((id == HOST_PLAYER_NETWORK_ID
						&& foundId != HOST_PLAYER_NETWORK_ID)
						|| (foundId == HOST_PLAYER_NETWORK_ID
								&& id != HOST_PLAYER_NETWORK_ID)) {
					// Host has hanged changed
					Log.info("Server",
							"Kick: Der Host einer geladenen Partie kann nicht verändert werden");
					con.sendTCP(new ServerRejectionMessage(
							"Der Host einer geladenen Partie kann nicht verändert werden"));
					con.close();
					// Server gets closed automatically
					return;
				}
				Log.info("Server",
						"Client als Teil der geladenen Partie erkannt");
				Player oldPlayer = savedGame.world.getPlayer(foundId);
				Character oldCharacter = savedGame.world.getCharacter(
						oldPlayer.getCurrentlyPlayedCharacterId());
				p = new LobbyPlayer(oldCharacter.getName(),
						oldCharacter.getSurname(), oldPlayer.getIcon(), -1,
						oldCharacter.isMale());
			}
		} else {
			p = PlayerUtils.getRandomPlayerWithUnusedProperties(playerStubs,
					players.values());
		}

		players.put(id, p);

		// Inform the other clients
		server.sendToAllExceptTCP(con.getID(),
				new PlayerJoinedMessage(id, players.get(id)));

		// Send the setup response
		con.sendTCP(new GameSetupMessage(players, id, sessionSetup, savedGame));
	}

	// ON PLAYER CHANGE
	private void onPlayerChange(Connection con, PlayerChangedMessage msg) {
		Log.debug("Server",
				"Die Konfiguration von Spieler %d hat sich geändert",
				msg.getId());
		server.sendToAllExceptTCP(con.getID(), msg);

		players.put(msg.getId(), msg.getPlayer());

		if (PlayerUtils.areAllPlayersReady(players.values())) {
			// Establish RMI connection
			establishRMIConnections();
			Log.info("Server",
					"RMI-Netzwerkverbindung zu den %d Clienten eingerichtet",
					players.size());

			// Close Broadcast-Server
			(new Thread(() -> {
				broadcastServer.close();
				broadcastServer = null;
				Log.info("Server", "Broadcast-Server geschlossen");
			})).start();
		}
	}

	private synchronized void establishRMIConnections() {
		session = new AuthoritativeSession(sessionSetup, serverSetup, players);

		// Register the RMI handler
		HashMap<Short, AuthoritativeResultListener> resultListeners = new HashMap<>();
		ObjectSpace.registerClasses(server.getKryo());
		ObjectSpace objectSpace = new ObjectSpace();
		objectSpace.register(254, session);

		for (Entry<Connection, Short> e : connections.entrySet()) {
			objectSpace.addConnection(e.getKey());

			AuthoritativeResultListener resultListener = ObjectSpace
					.getRemoteObject(e.getKey(), e.getValue(),
							AuthoritativeResultListener.class);
			if (resultListener == null) {
				Log.error("Server",
						"Der resultListener des Spielers %d ist null",
						e.getKey());
				break;
			}
			((RemoteObject) resultListener).setNonBlocking(true);
			resultListeners.put(e.getValue(), resultListener);
		}

		session.setResultListeners(resultListeners);
	}

	public interface IHostCallback {
		public void onHostStarted(Exception e);
	}

}
