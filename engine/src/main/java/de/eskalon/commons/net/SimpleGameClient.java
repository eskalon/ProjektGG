package de.eskalon.commons.net;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nullable;

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
import de.eskalon.commons.net.packets.chat.S2CChatMessageReceivedPacket;
import de.eskalon.commons.net.data.ChatMessage;
import de.eskalon.commons.net.packets.chat.C2SSendChatMessagePacke;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.handshake.S2CConnectionEstablishedPacket;
import de.eskalon.commons.net.packets.handshake.S2CConnectionRejectedPacket;
import de.eskalon.commons.net.packets.handshake.S2CLobbyJoinedPacket;
import de.eskalon.commons.net.packets.handshake.C2SRequestJoiningLobbyPacket;
import de.eskalon.commons.net.packets.sync.C2SChangeGameSetupPacket;
import de.eskalon.commons.net.packets.sync.C2SChangePlayerPacket;
import de.eskalon.commons.net.packets.sync.S2CLobbyDataChangedPacket;
import de.eskalon.commons.net.packets.sync.S2CLobbyDataChangedPacket.ChangeType;
import de.eskalon.commons.utils.MachineIdentificationUtils;

/**
 * A basic game client.
 * 
 * @see #connect(ICallback, String, String, int)
 * @see #disconnect()
 */
public abstract class SimpleGameClient<G, S, P> {

	private static final Logger LOG = LoggerService
			.getLogger(SimpleGameClient.class);

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

	protected ArrayList<ChatMessage<P>> chatMessages = new ArrayList<>();

	protected LobbyData<G, S, P> lobbyData;

	public SimpleGameClient() {
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
		listener.addTypeHandler(S2CConnectionEstablishedPacket.class,
				(con, msg) -> {
					client.sendTCP(new C2SRequestJoiningLobbyPacket(
							MachineIdentificationUtils.getHostname(),
							gameVersion));
				});
		// Lobby joined (= step 2)
		listener.addTypeHandler(S2CLobbyJoinedPacket.class, (con, msg) -> {
			localClientId = msg.getClientNetworkId();
			LOG.info("[CLIENT] Lobby joined. Local network ID is: %d",
					localClientId);
			this.lobbyData = msg.getLobbyData();
			callback.onSuccess(msg.getLobbyData());
			client.addListener(new Listener() {
				@Override
				public void disconnected(Connection connection) {
					LOG.info("[CLIENT] Disconnected!");
					onConnectionLost();

					//@formatter:off					
//					try {
//						client.dispose();
//					} catch (IOException e) {
//						LOG.warn(Exceptions.getStackTraceAsString(e));
//					}
					//@formatter:on
				}
			});
		});
		// Connection rejected
		listener.addTypeHandler(S2CConnectionRejectedPacket.class,
				(con, msg) -> {
					LOG.info(
							"[CLIENT] Couldn't connect: Client was rejected (%s)",
							msg.getMessage());
					callback.onFailure(msg.getMessage());
				});
		// Lobby data syncing
		listener.addTypeHandler(S2CLobbyDataChangedPacket.class, (con, msg) -> {
			LobbyData<G, S, P> tmp = this.lobbyData;
			this.lobbyData = msg.getLobbyData();

			onLobbyDataChanged(tmp, msg.getLobbyData(), msg.getChangeType());
		});
		// Chat messages
		listener.addTypeHandler(S2CChatMessageReceivedPacket.class,
				(con, msg) -> {
					ChatMessage chatMessage = new ChatMessage<>(
							lobbyData.getPlayers().get(msg.getSender()),
							msg.getMessage());
					chatMessages.add(chatMessage);
					onChatMessageReceived(chatMessage);
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

		ThreadHandler.instance().executeRunnable(() -> {
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

	public void disconnect() {
		LOG.info("[CLIENT] Disconnecting...");
		client.close();
	}

	/**
	 * Stops the client and disconnects from the server.
	 */
	public void stop() {
		LOG.info("[CLIENT] Stopping the client...");
		client.stop();
		LOG.info("[CLIENT] Client stopped!");
	}

	/**
	 * Needs to be called regularly to update the ping.
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

	public ArrayList<ChatMessage<P>> getChatMessages() {
		return chatMessages;
	}

	public void sendChatMessage(String message) {
		client.sendTCP(new C2SSendChatMessagePacke(message));
		chatMessages.add(new ChatMessage(
				lobbyData.getPlayers().get(localClientId), message));
	}

	public void changeLocalPlayerData(P d) {
		client.sendTCP(new C2SChangePlayerPacket<P>(d));
	}

	public void changeGameSetup(G sessionSetup, @Nullable S gameState) {
		client.sendTCP(
				new C2SChangeGameSetupPacket<G, S>(sessionSetup, gameState));
	}

	/* --- METHODS FOR CHILD CLASSES --- */
	protected abstract void onChatMessageReceived(ChatMessage msg);

	protected abstract void onLobbyDataChanged(LobbyData<G, S, P> oldData,
			LobbyData<G, S, P> newData, ChangeType changeType);

	protected abstract void onConnectionLost();

}
