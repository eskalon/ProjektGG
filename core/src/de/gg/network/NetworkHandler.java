package de.gg.network;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import de.gg.core.LobbyPlayer;
import de.gg.core.ProjektGG;
import de.gg.data.GameSessionSetup;
import de.gg.event.ConnectionEstablishedEvent;
import de.gg.event.GameSessionSetupEvent;
import de.gg.event.NewChatMessagEvent;
import de.gg.event.PlayerChangedEvent;
import de.gg.event.PlayerConnectedEvent;
import de.gg.event.PlayerDisconnectedEvent;
import de.gg.network.GameServer.IHostCallback;
import de.gg.network.message.ChatMessageSentMessage;
import de.gg.network.message.GameSetupMessage;
import de.gg.network.message.PlayerChangedMessage;
import de.gg.network.message.PlayerJoinedMessage;
import de.gg.network.message.PlayerLeftMessage;

public class NetworkHandler {

	private EventBus eventBus;
	private Client client;
	private GameServer server;
	/**
	 * The network ID of the local player.
	 */
	private short localClientId;

	public NetworkHandler(EventBus eventBus) {
		Preconditions.checkNotNull(eventBus, "Event handler cannot be null.");

		this.eventBus = eventBus;
	}

	/**
	 * Connects the client to the server. After it is finished a
	 * {@link ConnectionEstablishedEvent} is posted on the
	 * {@linkplain ProjektGG#getEventBus() event bus}.
	 * 
	 * @param ip
	 *            The server's ip.
	 * @param port
	 *            The server's port.
	 */
	public void setUpConnectionAsClient(String ip, int port) {
		client = new Client();
		client.start();

		NetworkRegisterer.registerClasses(client.getKryo());

		TypeListener listener = new TypeListener();
		// GAME SETUP MESSAGE (ON CLIENT CONNECT)
		listener.addTypeHandler(GameSetupMessage.class, (con, msg) -> {
			eventBus.post(new GameSessionSetupEvent(msg.getPlayers(),
					msg.getId(), msg.getSettings()));
			localClientId = msg.getId();
			Gdx.app.log("Client", "Netzwerk ID: " + localClientId);
		});
		// NEW CHAT MESSAGE
		listener.addTypeHandler(ChatMessageSentMessage.class, (con, msg) -> {
			eventBus.post(new NewChatMessagEvent(msg.getSenderId(),
					msg.getMessage()));
		});
		// PLAYER CHANGED
		listener.addTypeHandler(PlayerChangedMessage.class, (con, msg) -> {
			eventBus.post(new PlayerChangedEvent(msg.getId(), msg.getPlayer()));
		});
		// PLAYER JOINED
		listener.addTypeHandler(PlayerJoinedMessage.class, (con, msg) -> {
			eventBus.post(
					new PlayerConnectedEvent(msg.getId(), msg.getPlayer()));
		});
		// PLAYER LEFT
		listener.addTypeHandler(PlayerLeftMessage.class, (con, msg) -> {
			eventBus.post(new PlayerDisconnectedEvent(msg.getId()));
		});
		client.addListener(listener);

		Thread t = new Thread(new Runnable() {
			public void run() {
				IOException ex = null;
				try {
					client.connect(6000, ip, port);
					Gdx.app.log("Client", "Lobby beigetreten");
				} catch (IOException e) {
					ex = e;
					e.printStackTrace();
				}
				eventBus.post(new ConnectionEstablishedEvent(ex));
			}
		});
		t.start();
	}

	/**
	 * Sets up a server and a client asynchronously. After it is finished a
	 * {@link ConnectionEstablishedEvent} is posted on the
	 * {@linkplain ProjektGG#getEventBus() event bus}.
	 * 
	 * @param port
	 *            The used port.
	 * @see ClientNetworkHandler#setUpConnectionAsClient(String, int)
	 */
	public void setUpConnectionAsHost(int port, GameSessionSetup setup) {
		server = new GameServer(port, setup, new IHostCallback() {
			@Override
			public void onHostStarted(IOException e) {
				if (e == null) {
					setUpConnectionAsClient("localhost", port);
				} else {
					eventBus.post(new ConnectionEstablishedEvent(e));
				}
			}
		});
	}

	/**
	 * @return Whether this player is also hosting the server.
	 */
	public boolean isHost() {
		return server != null;
	}

	/**
	 * Disconnects the client.
	 */
	public void disconnect() {
		client.close();
		if (isHost())
			server.stop();
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

	public void sendChatMessage(String message) {
		sendObject(new ChatMessageSentMessage(localClientId, message));
	}

	/**
	 * Updates the player on the server.
	 */
	public void onLocalPlayerChange(LobbyPlayer player) {
		sendObject(new PlayerChangedMessage(localClientId, player));
	}

}
