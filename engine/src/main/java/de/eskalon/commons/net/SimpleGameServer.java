package de.eskalon.commons.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ConnectionListener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.ServerDiscoveryHandler;

import de.damios.guacamole.Exceptions;
import de.damios.guacamole.ICallback;
import de.damios.guacamole.Preconditions;
import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.net.packets.DiscoveryResponsePacket;
import de.eskalon.commons.net.packets.chat.ChatMessageReceivedPacket;
import de.eskalon.commons.net.packets.chat.SendChatMessagePacke;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.handshake.ConnectionEstablishedPacket;
import de.eskalon.commons.net.packets.handshake.ConnectionRejectedPacket;
import de.eskalon.commons.net.packets.handshake.LobbyJoinedPacket;
import de.eskalon.commons.net.packets.handshake.RequestJoiningLobbyPacket;
import de.eskalon.commons.net.packets.sync.ChangeGameSetupPacket;
import de.eskalon.commons.net.packets.sync.ChangePlayerPacket;
import de.eskalon.commons.net.packets.sync.LobbyDataChangedPacket;
import de.eskalon.commons.net.packets.sync.LobbyDataChangedPacket.ChangeType;

/**
 * A basic game server.
 *
 * @param <D>
 *            The type of player connected to this server.
 * @see #start(ICallback)
 * @see #stop()
 */
public abstract class SimpleGameServer<G, S, P> {

	private static final Logger LOG = LoggerService
			.getLogger(SimpleGameServer.class);

	public static final int DEFAULT_PORT = 55678;
	public static final int UDP_DISCOVER_PORT = 54678;
	/**
	 * The network ID of the local player.
	 */
	public static final short HOST_PLAYER_NETWORK_ID = 0;

	protected Server server;
	protected Server broadcastServer;
	protected ServerSettings serverSettings;

	/**
	 * A count of all joined players. Used to generate the player IDs.
	 */
	private short playerIdIterator = 0;
	protected LobbyData<G, S, P> lobbyData;

	/**
	 * Creates a server object with the specified settings.
	 *
	 * @param serverSettings
	 *            The server's settings, especially containing the port.
	 */
	public SimpleGameServer(ServerSettings serverSettings,
			LobbyData lobbyData) {
		Preconditions.checkNotNull(serverSettings,
				"server settings cannot be null");

		this.lobbyData = lobbyData;
		this.serverSettings = serverSettings;

		this.server = new Server();
		this.server.addListener(new ConnectionListener() {
			@Override
			public void connected(Connection con) {
				onClientConnected(con);
			}

			@Override
			public void disconnected(Connection con) {
				onClientDisconnected(con);
			}
		});
		TypeListener typeListener = new TypeListener();
		// Lobby joining
		typeListener.addTypeHandler(RequestJoiningLobbyPacket.class,
				(con, msg) -> onLobbyJoinRequest(con, msg));
		// Lobby data syncing
		typeListener.addTypeHandler(ChangePlayerPacket.class,
				(con, msg) -> onPlayerChange(con, msg));
		typeListener.addTypeHandler(ChangeGameSetupPacket.class, (con, msg) -> {
			if ((short) con.getArbitraryData() == HOST_PLAYER_NETWORK_ID) {
				lobbyData.setGameState(msg.getGameState());
				lobbyData.setSessionSetup(msg.getSessionSetup());
				server.sendToAllTCP(new LobbyDataChangedPacket(lobbyData,
						ChangeType.DATA_CHANGE));
			} else {
				LOG.error(
						"[SERVER] Non-host player %d tried to change the lobby data!",
						(short) con.getArbitraryData());
			}
		});
		// Chat messages
		typeListener.addTypeHandler(SendChatMessagePacke.class, (con, msg) -> {
			server.sendToAllExceptTCP(con.getID(),
					new ChatMessageReceivedPacket(
							(short) con.getArbitraryData(), msg.getMessage()));
		});
		server.addListener(typeListener);
	}

	/**
	 * Sets up a server asynchronously. After it is finished the callback is
	 * informed.
	 *
	 * @param callback
	 */
	public void start(ICallback callback) {
		Preconditions.checkNotNull(callback, "callback cannot be null");

		LOG.info("[SERVER] --- Server is starting ---");
		ThreadHandler.getInstance().executeRunnable(() -> {
			try {
				// Start the server
				server.bind(serverSettings.getPort());
				server.start();
				LOG.info("[SERVER] Server started");

				// Create & start the broadcast server
				if (serverSettings.isPublic()) {
					startBroadcastServer();
				}
				callback.onSuccess(null); // Host successfully started
			} catch (IOException | IllegalArgumentException e2) {
				LOG.error("[SERVER] Server could not be started: %s",
						Exceptions.getStackTraceAsString(e2));
				callback.onFailure(e2); // Something went wrong
			}
		});
	}

	private void startBroadcastServer() {
		broadcastServer = new Server();
		broadcastServer.getKryo().register(DiscoveryResponsePacket.class);
		broadcastServer.setDiscoveryHandler(new ServerDiscoveryHandler() {
			@Override
			public boolean onDiscoverHost(DatagramChannel datagramChannel,
					InetSocketAddress fromAddress) throws IOException {
				DiscoveryResponsePacket packet = new DiscoveryResponsePacket(
						serverSettings.getPort(), serverSettings.getGameName(),
						server.getConnections().size(),
						serverSettings.getMaxPlayerCount());

				ByteBuffer buffer = ByteBuffer.allocate(256);
				broadcastServer.getSerialization().write(null, buffer, packet);
				buffer.flip();

				datagramChannel.send(buffer, fromAddress);

				return true;
			}
		});

		try {
			broadcastServer.bind(0, UDP_DISCOVER_PORT);
			broadcastServer.start();
			LOG.info("[SERVER] Broadcast server started");
		} catch (IOException e1) {
			LOG.error("[SERVER] Broadcast server couldn't be started: %s",
					Exceptions.getStackTraceAsString(e1));
		}
	}

	private synchronized void onClientConnected(Connection con) {
		if (server.getConnections().size() >= serverSettings
				.getMaxPlayerCount()) { // Lobby is full
			LOG.info("[SERVER] Client was rejected for want of capacity");

			con.sendTCP(
					new ConnectionRejectedPacket(Lang.get("server.is_full")));
			con.close();
		} else { // Still free slots
			LOG.info("[SERVER] Client accepted");

			con.setArbitraryData(playerIdIterator);
			con.sendTCP(new ConnectionEstablishedPacket());
			playerIdIterator++;
		}
	}

	private synchronized void onClientDisconnected(Connection con) {
		if (con.getArbitraryData() != null) {
			short id = (short) con.getArbitraryData();

			LOG.info("[SERVER] Client %d disconnected", id);

			if (lobbyData.getPlayers().containsKey(id)) {
				lobbyData.getPlayers().remove(id);
				server.sendToAllTCP(new LobbyDataChangedPacket(lobbyData,
						ChangeType.PLAYER_LEFT));
			}
		}
	}

	private synchronized void onLobbyJoinRequest(Connection con,
			RequestJoiningLobbyPacket msg) {
		short id = (short) con.getArbitraryData();

		if (!serverSettings.getVersion().equals(msg.getVersion())) {
			LOG.info("[SERVER] Kick: Version mismatch (%s)", msg.getVersion());
			con.sendTCP(new ConnectionRejectedPacket(
					Lang.get("dialog.connecting_failed.version_mismatch")));
			con.close();
			return;
		}

		P newPlayerData = createPlayerData(id, msg.getHostname());
		lobbyData.getPlayers().put(id, newPlayerData);

		LOG.info("[SERVER] Client %d was registered as new player", id);

		con.sendTCP(new LobbyJoinedPacket(id, lobbyData));
		server.sendToAllExceptTCP(con.getID(), new LobbyDataChangedPacket(
				lobbyData, ChangeType.PLAYER_JOINED));
	}

	protected void onPlayerChange(Connection con, ChangePlayerPacket msg) {
		lobbyData.getPlayers().put((short) con.getArbitraryData(),
				(P) msg.getPlayerData());
		server.sendToAllTCP(
				new LobbyDataChangedPacket(lobbyData, ChangeType.DATA_CHANGE));
	}

	/**
	 * Stops the server. Also takes care of saving the game.
	 */
	public void stop() {
		LOG.info("[SERVER] Stopping the server...");
		server.stop();
		if (broadcastServer != null)
			stopBroadcastServer();

		LOG.info("[SERVER] Server stopped!");
	}

	protected void stopBroadcastServer() {
		broadcastServer.stop();
		broadcastServer = null;
	}

	public ServerSettings getServerSetup() {
		return serverSettings;
	}

	/* --- METHODS FOR CHILD CLASSES --- */
	/**
	 * This method is called to set up a new player. It is called upon receiving
	 * a {@link RequestJoiningLobbyPacket} which is sent after a
	 * {@link ConnectionEstablishedPacket connection with a client was
	 * established}.
	 * 
	 * @param id
	 * @param hostname
	 * 
	 * @return a player data object
	 * 
	 */
	protected abstract P createPlayerData(short id, String hostname);

}
