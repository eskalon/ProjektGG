package de.gg.engine.network;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.google.common.base.Preconditions;

import de.gg.engine.log.Log;
import de.gg.engine.network.message.ClientHandshakeMessage;
import de.gg.engine.network.message.ServerAcceptanceMessage;
import de.gg.engine.network.message.ServerHandshakeMessage;
import de.gg.engine.network.message.ServerRejectionMessage;
import de.gg.engine.utils.MachineIdentificationUtils;

/**
 * This class takes care of handling the networking part for a basic game
 * client.
 * 
 * @see #connect(IClientConnectCallback, String, String, int)
 * @see #disconnect()
 */
public abstract class BaseGameClient {
	protected Client client;
	/**
	 * The network ID of the local player.
	 */
	protected short localClientId;

	private float timeSinceLastPingUpdate = 0;
	/**
	 * The interval after which the ping gets updated again (in seconds).
	 */
	private final int PING_UPDATE_INTERVAL = 10;
	/**
	 * The last measured ping.
	 */
	private int ping;

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
	public void connect(IClientConnectCallback callback, String gameVersion,
			String ip, int port) {
		Preconditions.checkNotNull(gameVersion, "gameVersion cannot be null.");
		Preconditions.checkNotNull(ip, "ip cannot be null.");

		Log.info("Client", "--- Neuem Spiel wird beigetreten ---");

		client = new Client();
		client.start();

		TypeListener listener = new TypeListener();
		// CLIENT CONNECTION
		// On Server acceptance (stage 1 of connection)
		listener.addTypeHandler(ServerAcceptanceMessage.class, (con, msg) -> {
			if (gameVersion.equals(msg.getServerVersion())) { // right server
																// version
				client.sendTCP(new ClientHandshakeMessage(
						MachineIdentificationUtils.getHostname())); // send
																	// handshake
			} else { // wrong server version
				Log.info("Client",
						"Fehler beim Verbinden: Falsche Server-Version (%s)",
						msg.getServerVersion());
				callback.onClientConnected(
						"Falsche Server-Version: " + msg.getServerVersion());
				con.close();
			}

		});
		// Server full
		listener.addTypeHandler(ServerRejectionMessage.class, (con, msg) -> {
			Log.info("Client", "Fehler beim Verbinden (Rejection): %s",
					msg.getMessage());
			callback.onClientConnected(msg.getMessage());
		});
		// Server handshake (stage 2 of connection)
		listener.addTypeHandler(ServerHandshakeMessage.class, (con, msg) -> {
			if (msg.wasSuccessful()) {
				Log.info("Client", "Verbinden war erfolgreich. Netzwerk-ID: %d",
						localClientId);
				localClientId = msg.getClientNetworkId();
				onSuccessfulHandshake();
				callback.onClientConnected(null); // Successful handshake
			} else {
				Log.info("Client", "Fehler beim Verbinden (Handshake): %s",
						msg.getMsg());
				callback.onClientConnected(msg.getMsg());
			}
		});
		// Ping
		listener.addTypeHandler(Ping.class, (con, msg) -> {
			if (msg.isReply) {
				this.ping = con.getReturnTripTime();
				Log.info("Client", "Ping: %d", ping);
			}
		});

		onCreation();

		client.addListener(listener);
		client.addListener(new Listener() {
			@Override
			public void disconnected(Connection connection) {
				Log.error("Client", "Verbindung beendet");
				onDisconnection();
			}
		});

		final Thread connectingThread = new Thread(() -> {
			try {
				client.connect(6000, ip, port);
				// A successful connection further requires a proper handshake
			} catch (IOException e) {
				Log.error("Client", "Fehler beim Verbinden: ", e);
				callback.onClientConnected(e.toString());
			}
		});
		connectingThread.start();
	}

	/**
	 * Disconnects the client from the server.
	 */
	public void disconnect() {
		Log.info("Client", "Trenne Verbindung...");
		client.close();
		Log.info("Client", "Verbindung getrennt!");
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
	 * This method is called when the {@link #client} is created. This is the
	 * place to register the networked classes to the {@linkplain Kryo kryo}
	 * serializer.
	 */
	protected abstract void onCreation();

	/**
	 * This method is called when the server handshake is successful and
	 * therefore the connection is fully established.
	 */
	protected abstract void onSuccessfulHandshake();

	protected abstract void onDisconnection();

}
