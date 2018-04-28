package de.gg.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map.Entry;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ConnectionListener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.ServerDiscoveryHandler;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

import de.gg.core.ProjektGG;
import de.gg.event.ConnectionEstablishedEvent;
import de.gg.game.AuthoritativeSession;
import de.gg.game.GameSession;
import de.gg.game.data.GameSessionSetup;
import de.gg.network.message.ChatMessageSentMessage;
import de.gg.network.message.DiscoveryResponsePacket;
import de.gg.network.message.GameSetupMessage;
import de.gg.network.message.PlayerChangedMessage;
import de.gg.network.message.PlayerJoinedMessage;
import de.gg.network.message.PlayerLeftMessage;
import de.gg.network.message.ServerFullMessage;
import de.gg.network.rmi.AuthoritativeResultListener;
import de.gg.network.rmi.SlaveActionListener;
import de.gg.util.CollectionUtils;
import de.gg.util.Log;
import de.gg.util.PlayerUtils;

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
	/**
	 * A count of all joined players. Used to generate the player IDs.
	 */
	private short playerIdIterator = 0;
	private HashMap<Short, LobbyPlayer> players;
	private HashMap<Short, Connection> connections;

	/**
	 * Sets up a server and a client asynchronously. After it is finished a
	 * {@link ConnectionEstablishedEvent} is posted on the
	 * {@linkplain ProjektGG#getEventBus() event bus}.
	 * 
	 * @param serverSetup
	 *            The server's settings, especially containing the port.
	 * @param sessionSetup
	 *            The game session's setup.
	 * @see ClientNetworkHandler#setUpConnectionAsClient(String, int)
	 */
	public GameServer(ServerSetup serverSetup, GameSessionSetup sessionSetup,
			IHostCallback callback) {
		this.players = new HashMap<>();
		this.connections = new HashMap<>();

		this.sessionSetup = sessionSetup;
		this.serverSetup = serverSetup;

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
		// CHAT MESSAGE
		typeListener.addTypeHandler(ChatMessageSentMessage.class,
				(con, msg) -> {
					server.sendToAllExceptTCP(con.getID(), msg);
				}

		);
		// PLAYER CHANGED
		typeListener.addTypeHandler(PlayerChangedMessage.class,
				(con, msg) -> onPlayerChange(con, msg));

		this.server.addListener(typeListener);

		Thread t = new Thread(new Runnable() {
			public void run() {
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
						broadcastServer.setDiscoveryHandler(
								new ServerDiscoveryHandler() {
									@Override
									public boolean onDiscoverHost(
											DatagramChannel datagramChannel,
											InetSocketAddress fromAddress)
											throws IOException {
										DiscoveryResponsePacket packet = new DiscoveryResponsePacket(
												serverSetup.getPort(),
												serverSetup.getGameName(),
												players.size());

										ByteBuffer buffer = ByteBuffer
												.allocate(256);
										broadcastServer
												.getSerializationFactory()
												.newInstance(null)
												.write(buffer, packet);
										buffer.flip();

										datagramChannel.send(buffer,
												fromAddress);

										return true;
									}
								});

						try {
							broadcastServer.bind(0, UDP_DISCOVER_PORT);
							Log.info("Server", "Broadcast-Server gestartet");
						} catch (IOException e) {
							Log.error("Server",
									"Der Broadcast-Server konnte nicht gestartet werden: %s",
									e);
						}
					}
					callback.onHostStarted(null);
				} catch (IOException e) {
					callback.onHostStarted(e);
					Log.error("Server",
							"Der Server konnte nicht gestartet werden: %s", e);
				}
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
	 * Sets up the game session.
	 * 
	 * @see GameSession#setupGame()
	 */
	public void setupGameSession() {
		session.setupGame();
		Log.info("Server", "Spiel gestartet");
	}

	/**
	 * Stops the server. Also takes care of saving the game.
	 */
	public void stop() {
		if (session != null)
			session.saveGame();
		server.stop();
		if (broadcastServer != null)
			broadcastServer.stop();
	}

	// LISTENER METHDOS
	// ON NEW CONNECTION
	private synchronized void onNewConnection(Connection con) {
		Log.info("Server", "Client verbunden");

		if (players.size() >= serverSetup.getMaxPlayerCount()) { // Match full
			con.sendTCP(new ServerFullMessage());
		} else { // Still free slots
			LobbyPlayer p = PlayerUtils.getRandomPlayer(players.values());
			players.put(playerIdIterator, p);
			connections.put(playerIdIterator, con);

			// Inform the other clients
			server.sendToAllExceptTCP(con.getID(), new PlayerJoinedMessage(
					playerIdIterator, players.get(playerIdIterator)));

			// Send the setup response
			con.sendTCP(new GameSetupMessage(players, playerIdIterator,
					sessionSetup, serverSetup.getVersion()));

			playerIdIterator++;
		}
	}

	// ON DISCONNECT
	private synchronized void onDisconnect(Connection con) {
		Short id = CollectionUtils.getKeyByValue(connections, con);

		if (id != null) {
			Log.info("Server", "Spieler %d hat die Verbindung getrennt", id);

			server.sendToAllExceptTCP(con.getID(), new PlayerLeftMessage(id));

			session.getResultListeners().remove(id);
			connections.remove(id);
			players.remove(id);
		}
	}

	// ON PLAYER CHANGE
	private void onPlayerChange(Connection con, PlayerChangedMessage msg) {
		server.sendToAllExceptTCP(con.getID(), msg);

		players.put(msg.getId(), msg.getPlayer());

		if (PlayerUtils.areAllPlayersReady(players.values())) {
			establishRMIConnections();

			broadcastServer.close();
			broadcastServer = null;
			Log.info("Server", "Broadcast-Server geschlossen");

		}
	}

	private void establishRMIConnections() {
		session = new AuthoritativeSession(sessionSetup, serverSetup, players);

		// Register the RMI handler
		HashMap<Short, AuthoritativeResultListener> resultListeners = new HashMap<>();
		ObjectSpace.registerClasses(server.getKryo());
		ObjectSpace objectSpace = new ObjectSpace();
		objectSpace.register(254, (SlaveActionListener) session);

		for (Entry<Short, Connection> e : connections.entrySet()) {
			objectSpace.addConnection(e.getValue());

			AuthoritativeResultListener resultListener = ObjectSpace
					.getRemoteObject(e.getValue(), e.getKey(),
							AuthoritativeResultListener.class);
			resultListeners.put(e.getKey(), resultListener);

			if (resultListener == null)
				Log.error("Server",
						"Der resultListener des Spielers %d ist null",
						e.getKey());

		}

		session.setResultListeners(resultListeners);
		Log.info("Server",
				"RMI-Netzwerkverbindung zu den Clienten eingerichtet");
	}

	public interface IHostCallback {
		public void onHostStarted(IOException e);
	}

}
