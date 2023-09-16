package de.gg.engine.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;

import javax.annotation.Nullable;

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
import de.gg.engine.network.message.ConnectionEstablishedMessage;
import de.gg.engine.network.message.ConnectionRejectedMessage;
import de.gg.engine.network.message.DiscoveryResponsePacket;
import de.gg.engine.network.message.LobbyJoinRequestMessage;
import de.gg.engine.network.message.LobbyJoinedMessage;

/**
 * A basic game server.
 *
 * @param <D>
 *            The type of player connected to this server.
 * @see #start(ICallback)
 * @see #stop()
 */
public abstract class BaseGameServer<D> {

	private static final Logger LOG = LoggerService
			.getLogger(BaseGameServer.class);

	public static final int DEFAULT_PORT = 55678;
	public static final int UDP_DISCOVER_PORT = 54678;
	/**
	 * The network ID of the local player.
	 */
	public static final short HOST_PLAYER_NETWORK_ID = 0;

	protected Server server;
	private Server broadcastServer;
	protected ServerSettings serverSettings;

	/**
	 * A count of all joined players. Used to generate the player IDs.
	 */
	private short playerIdIterator = 0;

	/**
	 * A hashmap of all connected players, keyed by their ID.
	 */
	protected HashMap<Short, D> players;

	/**
	 * Creates a server object with the specified settings.
	 *
	 * @param serverSetup
	 *            The server's settings, especially containing the port.
	 */
	public BaseGameServer(ServerSettings serverSettings) {
		Preconditions.checkNotNull(serverSettings,
				"server settings cannot be null");

		this.players = new HashMap<>();
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
		typeListener.addTypeHandler(LobbyJoinRequestMessage.class,
				(con, msg) -> onLobbyJoinRequest(con, msg));
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
				LOG.error("[Server] Server could not be started: %s",
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
						players.size(), serverSettings.getMaxPlayerCount());

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
		if (players.size() >= serverSettings.getMaxPlayerCount()) { // Lobby is
																	// full
			LOG.info("[SERVER] Client was rejected for want of capacity");

			con.sendTCP(
					new ConnectionRejectedMessage(Lang.get("server.is_full")));
			con.close();
		} else { // Still free slots
			LOG.info("[SERVER] Client accepted");

			con.setArbitraryData(playerIdIterator);
			con.sendTCP(new ConnectionEstablishedMessage());
			playerIdIterator++;
		}
	}

	private synchronized void onClientDisconnected(Connection con) {
		if (con.getArbitraryData() != null) {
			short id = (short) con.getArbitraryData();

			LOG.info("[SERVER] Client %d disconnected", id);

			if (players.containsKey(id)) {
				onPlayerDisconnected(id);

				players.remove(id);
			}
		}
	}

	private synchronized void onLobbyJoinRequest(Connection con,
			LobbyJoinRequestMessage msg) {
		short id = (short) con.getArbitraryData();
		String response = handleLobbyJoinRequest(id, msg);

		if (response == null) {
			con.sendTCP(new LobbyJoinedMessage(id));
		} else {
			con.sendTCP(new ConnectionRejectedMessage(response));
			con.close();
		}
	}

	/**
	 * Stops the server. Also takes care of saving the game.
	 */
	public void stop() {
		LOG.info("[SERVER] Server is stopping...");
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

	/* --- Methods for child classes --- */
	/**
	 * This method is called when a {@link LobbyJoinRequestMessage} is received
	 * after a {@link ConnectionEstablishedMessage connection was established}.
	 * 
	 * @param msg
	 * 
	 * @return {@code null} if the lobby join request is successful; an error
	 *         message otherwise
	 * 
	 */
	protected abstract @Nullable String handleLobbyJoinRequest(short id,
			LobbyJoinRequestMessage msg);

	protected abstract void onPlayerDisconnected(short id);

}
