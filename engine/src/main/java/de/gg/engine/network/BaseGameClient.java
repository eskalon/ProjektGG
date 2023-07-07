package de.gg.engine.network;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import de.damios.guacamole.ICallback;
import de.damios.guacamole.Preconditions;
import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.utils.MachineIdentificationUtils;
import de.gg.engine.network.message.LobbyJoinRequestMessage;
import de.gg.engine.network.message.LobbyJoinedMessage;
import de.gg.engine.network.message.ConnectionRejectedMessage;
import de.gg.engine.network.message.ConnectionEstablishedMessage;

/**
 * A basic game client.
 * 
 * @see #connect(ICallback, String, String, int)
 * @see #disconnect()
 */
public abstract class BaseGameClient {

	private static final Logger LOG = LoggerService
			.getLogger(BaseGameClient.class);

	protected Client client;
	/**
	 * The network ID of the local player.
	 */
	protected short localClientId;

	/**
	 * The interval after which the ping gets updated again (in seconds).
	 */
	private final float PING_UPDATE_INTERVAL = 10F; // 2F
	private float timeSinceLastPingUpdate = 0;
	private int ping;

	public BaseGameClient() {
		this.client = new Client();
	}

	/**
	 * Tries to connect the client to the server asynchronously. After it is
	 * finished the given listener is called.
	 *
	 * @param callback
	 *            The listener callback.
	 * @param gameVersion
	 *            the client's game version.
	 * @param ip
	 *            The server's ip address.
	 * @param port
	 *            The server's port.
	 */
	public void connect(ICallback callback, String gameVersion, String ip,
			int port) {
		Preconditions.checkNotNull(callback, "callback cannot be null.");
		Preconditions.checkNotNull(gameVersion, "game version cannot be null.");
		Preconditions.checkNotNull(ip, "ip cannot be null.");

		/* Listeners */
		TypeListener listener = new TypeListener();
		// Connection established (= step 1)
		listener.addTypeHandler(ConnectionEstablishedMessage.class,
				(con, msg) -> {
					client.sendTCP(new LobbyJoinRequestMessage(
							MachineIdentificationUtils.getHostname(),
							gameVersion));
				});
		// Lobby joined (= step 2)
		listener.addTypeHandler(LobbyJoinedMessage.class, (con, msg) -> {
			localClientId = msg.getClientNetworkId();
			LOG.info("[CLIENT] Connection established. Local network ID is: %d",
					localClientId);
			onLobbyJoined();
			client.addListener(new Listener() {
				@Override
				public void disconnected(Connection connection) {
					LOG.info("[CLIENT] Connection closed!");
					onConnectionClosed();
				}
			});
			callback.onSuccess(null);
		});
		// Connection rejected
		listener.addTypeHandler(ConnectionRejectedMessage.class, (con, msg) -> {
			LOG.info("[CLIENT] Couldn't connect: Client was rejected (%s)",
					msg.getMessage());
			callback.onFailure(msg.getMessage());
		});
		// Ping
		listener.addTypeHandler(Ping.class, (con, msg) -> {
			if (msg.isReply) {
				this.ping = con.getReturnTripTime();
				LOG.debug("[CLIENT] Ping: %d", ping);
			}
		});
		client.addListener(listener);

		LOG.info("[CLIENT] --- Connecting to Server ---");

		ThreadHandler.getInstance().executeRunnable(() -> {
			try {
				client.start();
				client.connect(6000, ip, port);
				// The client now tries to establish a connection
				// (ConnectionEstablishedMessage) and to join the lobby
				// (LobbyJoinRequestMessage, LobbyJoinedMessage)
			} catch (IOException e) {
				LOG.error("[CLIENT] Couldn't connect: %s", e);
				callback.onFailure("Couldn't connect: " + e.getMessage());
			}
		});
	}

	/**
	 * Disconnects the client from the server.
	 */
	public void disconnect() {
		LOG.info("[CLIENT] Closing connection...");
		client.close();
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
	 * Returns the last measured ping. Only gets updated if
	 * {@link #updatePing(float)} is called regularly.
	 *
	 * @return The last measured ping.
	 */
	public int getPing() {
		return ping;
	}

	public short getLocalNetworkID() {
		return localClientId;
	}

	/* --- Methods for child classes --- */
	/**
	 * This method is called when the client has successfully joined a lobby.
	 */
	protected abstract void onLobbyJoined();

	protected abstract void onConnectionClosed();

}
