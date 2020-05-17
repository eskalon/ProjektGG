package de.gg.engine.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ConnectionListener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.ServerDiscoveryHandler;
import com.google.common.base.Preconditions;

import de.eskalon.commons.log.Log;
import de.eskalon.commons.utils.ISuccessCallback;
import de.gg.engine.misc.ThreadHandler;
import de.gg.engine.network.message.ClientHandshakeRequest;
import de.gg.engine.network.message.DiscoveryResponsePacket;
import de.gg.engine.network.message.ServerAcceptanceResponse;
import de.gg.engine.network.message.ServerRejectionResponse;
import de.gg.engine.network.message.SuccessfulHandshakeResponse;

/**
 * The basic implementation of a game server.
 *
 * @param <C>
 *            The type of player connected to this server.
 * @see #start(ISuccessCallback)
 * @see #stop()
 */
public abstract class BaseGameServer<C> {

	public static final int DEFAULT_PORT = 55678;
	public static final int UDP_DISCOVER_PORT = 54678;
	/**
	 * The network id of the local player. Is always <code>0</code>.
	 */
	public static final short HOST_PLAYER_NETWORK_ID = 0;
	protected Server server;
	private Server broadcastServer;

	protected ServerSetup serverSetup;

	public HashMap<Short, String> clientIdentifiers;

	/**
	 * A count of all joined players. Used to generate the player IDs.
	 */
	private short playerIdIterator = 0;
	/**
	 * A hashmap of all connected players, keyed by their ID.
	 */
	protected HashMap<Short, C> players;
	/**
	 * Maps the connections to the player IDs.
	 */
	protected HashMap<Connection, Short> connections;

	/**
	 * Creates a server object with the specified settings.
	 *
	 * @param serverSetup
	 *            The server's settings, especially containing the port.
	 */
	public BaseGameServer(ServerSetup serverSetup) {
		Preconditions.checkNotNull(serverSetup, "server setup cannot be null");

		this.players = new HashMap<>();
		this.connections = new HashMap<>();
		this.serverSetup = serverSetup;
	}

	/**
	 * Sets up a server asynchronously. After it is finished the callback is
	 * informed.
	 *
	 * @param callback
	 *            the callback that is informed when the server is started.
	 */
	public void start(ISuccessCallback callback) {
		Preconditions.checkNotNull(callback, "callback cannot be null.");

		Log.info("Server", "--- Server is starting ---");

		this.server = new Server();
		this.server.start();

		// ON NEW CONNECTION & ON DICONNECTED
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
		typeListener.addTypeHandler(ClientHandshakeRequest.class,
				(con, msg) -> onClientHandshake(con, msg));
		server.addListener(typeListener);

		onCreation();

		ThreadHandler.getInstance().executeRunnable(() -> {
			try {
				// Start the server
				server.bind(serverSetup.getPort());
				Log.info("Server", "Server started");

				// Create & start the broadcast server
				if (serverSetup.isPublic()) {
					startBroadcastServer();
				}
				callback.onSuccess(null); // Host successfully started
			} catch (IOException | IllegalArgumentException e2) {
				Log.error("Server",
						"Server could not be started: %s", e2);
				callback.onFailure(e2); // Something went wrong
			}
		});
	}

	private void startBroadcastServer() {
		broadcastServer = new Server();
		broadcastServer.start();
		broadcastServer.getKryo().register(DiscoveryResponsePacket.class);
		broadcastServer.setDiscoveryHandler(new ServerDiscoveryHandler() {
			@Override
			public boolean onDiscoverHost(DatagramChannel datagramChannel,
					InetSocketAddress fromAddress) throws IOException {
				DiscoveryResponsePacket packet = new DiscoveryResponsePacket(
						serverSetup.getPort(), serverSetup.getGameName(),
						players.size(), serverSetup.getMaxPlayerCount());

				ByteBuffer buffer = ByteBuffer.allocate(256);
				broadcastServer.getSerialization().write(null, buffer, packet);
				buffer.flip();

				datagramChannel.send(buffer, fromAddress);

				return true;
			}
		});

		try {
			broadcastServer.bind(0, UDP_DISCOVER_PORT);
			Log.info("Server", "Broadcast server started");
		} catch (IOException e1) {
			Log.error("Server", "Broadcast server couldn't be started: %s", e1);
		}
	}

	private synchronized void onClientConnected(Connection con) {
		if (players.size() >= serverSetup.getMaxPlayerCount()) { // Match full
			Log.info("Server", "Client was rejected for want of capacity");

			con.sendTCP(
					new ServerRejectionResponse("Der Server ist bereits voll"));
			con.close();
		} else { // Still free slots
			Log.info("Server", "Client accepted");

			connections.put(con, playerIdIterator);
			con.sendTCP(new ServerAcceptanceResponse());
			// onPlayerConnected(playerIdIterator);
			playerIdIterator++;
		}
	}

	private synchronized void onClientDisconnected(Connection con) {
		Short id = connections.remove(con);

		if (id != null) {
			Log.info("Server", "Client %d has disconnected", id);

			if (players.containsKey(id)) {
				onPlayerDisconnected(con, id);

				players.remove(id);
			}
		}
	}

	/**
	 * Stops the server. Also takes care of saving the game.
	 */
	public void stop() {
		Log.info("Server", "Server is stopping...");
		server.stop();
		if (broadcastServer != null)
			stopBroadcastServer();

		Log.info("Server", "Server stopped!");
	}

	protected void stopBroadcastServer() {
		broadcastServer.stop();
		broadcastServer = null;
	}

	public ServerSetup getServerSetup() {
		return serverSetup;
	}

	/* --- Methods for child classes --- */
	/**
	 * This method is called when the {@link #server} is created. This is the
	 * place to register the networked classes to the {@linkplain Kryo kryo}
	 * serializer.
	 */
	protected abstract void onCreation();

	protected abstract void onPlayerDisconnected(Connection con, short id);

	/**
	 * This method is called when the client sends its handshake message.
	 * <p>
	 * Subclasses have to send a {@link SuccessfulHandshakeResponse} back,
	 * denoting whether the handshake was a success.
	 * 
	 * @param con
	 *            The connection of the client initiating the handshake.
	 * @param msg
	 *            The actual handshake message.
	 */
	protected abstract void onClientHandshake(Connection con,
			ClientHandshakeRequest msg);

}
