package de.gg.engine.network;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.google.common.base.Preconditions;

import de.damios.guacamole.ISuccessCallback;
import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.Log;
import de.eskalon.commons.utils.MachineIdentificationUtils;
import de.gg.engine.network.message.ClientHandshakeRequest;
import de.gg.engine.network.message.FailedHandshakeResponse;
import de.gg.engine.network.message.ServerAcceptanceResponse;
import de.gg.engine.network.message.ServerRejectionResponse;
import de.gg.engine.network.message.SuccessfulHandshakeResponse;

/**
 * This class takes care of handling the networking part for a basic game
 * client.
 * 
 * @see #connect(ISuccessCallback, String, String, int)
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
	public void connect(ISuccessCallback callback, String gameVersion,
			String ip, int port) {
		Preconditions.checkNotNull(callback, "callback cannot be null.");
		Preconditions.checkNotNull(gameVersion, "game version cannot be null.");
		Preconditions.checkNotNull(ip, "ip cannot be null.");

		Log.info("Client", "--- Connecting to Server ---");

		client = new Client();
		client.start();

		TypeListener listener = new TypeListener();
		// CLIENT CONNECTION
		// On Server acceptance (stage 1 of connection)
		listener.addTypeHandler(ServerAcceptanceResponse.class, (con, msg) -> {
			client.sendTCP(new ClientHandshakeRequest(
					MachineIdentificationUtils.getHostname(), gameVersion));
		});
		// Server full
		listener.addTypeHandler(ServerRejectionResponse.class, (con, msg) -> {
			Log.info("Client", "Couldn't connect: Client was rejected (%s)",
					msg.getMessage());
			callback.onFailure(msg.getMessage());
		});
		// Server handshake (stage 2 of connection)
		listener.addTypeHandler(SuccessfulHandshakeResponse.class,
				(con, msg) -> {
					Log.info("Client",
							"Connection established. Local network ID is: %d",
							localClientId);
					localClientId = msg.getClientNetworkId();
					onSuccessfulHandshake();
					client.addListener(new Listener() {
						@Override
						public void disconnected(Connection connection) {
							Log.info("Client", "Connection closed!");
							onDisconnection();
						}
					});
					callback.onSuccess(null);
				});
		listener.addTypeHandler(FailedHandshakeResponse.class, (con, msg) -> {
			Log.info("Client",
					"Couldn't connect: Handshake was not successful (%s)",
					msg.getMsg());
			callback.onFailure(msg.getMsg());
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

		ThreadHandler.getInstance().executeRunnable(() -> {
			try {
				client.connect(6000, ip, port);
				// A successful connection further requires a proper handshake
			} catch (IOException e) {
				Log.error("Client", "Couldn't connect: %s", e);
				callback.onFailure("Couldn't connect: " + e.getMessage());
			}
		});
	}

	/**
	 * Disconnects the client from the server.
	 */
	public void disconnect() {
		Log.info("Client", "Closing connection...");
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
	 * This method is called when the {@link #client} is created. This is the
	 * place to register the networked classes with the {@linkplain Kryo
	 * serializer}.
	 */
	protected abstract void onCreation();

	/**
	 * This method is called when the server handshake is successful and
	 * therefore the connection is fully established.
	 */
	protected abstract void onSuccessfulHandshake();

	protected abstract void onDisconnection();

}
