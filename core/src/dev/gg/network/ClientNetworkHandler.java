package dev.gg.network;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import dev.gg.callback.IClientCallback;
import dev.gg.core.Player;
import dev.gg.network.event.ClientEventHandler;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.GameSetupMessage;
import dev.gg.network.message.PlayerChangedMessage;
import dev.gg.network.message.PlayerJoinedMessage;
import dev.gg.network.message.PlayerLeftMessage;
import dev.gg.network.message.SeverTurnMessage;

/**
 * This class takes care of the actual networking part for the client without
 * interest in the game logic.
 */
public class ClientNetworkHandler {

	private Client client;
	private MultiplayerSession session;
	private ClientEventHandler eventHandler;
	private IOException ex;

	public ClientNetworkHandler(MultiplayerSession session) {
		this.session = session;
	}

	/**
	 * Connects a client asynchronously. After it is finished the appropriate
	 * {@linkplain IClientCallback#onClientStarted(IOException) callback method}
	 * is invoked.
	 * 
	 * @param ip
	 *            The server's ip.
	 * @param port
	 *            The server's port.
	 * @param callback
	 *            The callback.
	 */
	public void connect(String ip, int port, IClientCallback callback) {
		client = new Client();
		client.start();

		NetworkRegisterer.registerClasses(client.getKryo());

		TypeListener listener = new TypeListener();
		listener.addTypeHandler(GameSetupMessage.class, (con, msg) -> {
			Gdx.app.log("Client",
					"Lobby beigetreten (ID: " + msg.getId() + ")");
			session.init(msg.getPlayers(), msg.getId(), msg.getSettings());
		});
		listener.addTypeHandler(ChatMessageSentMessage.class, (con, msg) -> {
			if (eventHandler != null)
				eventHandler.onNewChatMessage(msg.getSenderId(),
						msg.getMessage());
		});
		listener.addTypeHandler(PlayerChangedMessage.class, (con, msg) -> {
			session.getPlayers().put(msg.getId(), msg.getPlayer());

			if (eventHandler != null)
				eventHandler.onPlayerChanged();
		});
		listener.addTypeHandler(PlayerJoinedMessage.class, (con, msg) -> {
			session.getPlayers().put(msg.getId(), msg.getPlayer());

			if (eventHandler != null)
				eventHandler.onPlayerConnect(msg.getPlayer());
		});
		listener.addTypeHandler(PlayerLeftMessage.class, (con, msg) -> {
			Player p = session.getPlayers().get(msg.getId());
			session.getPlayers().remove(msg.getId());

			if (eventHandler != null)
				eventHandler.onPlayerDisconnect(p);
		});
		listener.addTypeHandler(SeverTurnMessage.class, (con, msg) -> {
			System.out.println("[Client] Server turn message received for turn " + msg.getTurn());
			session.addNewCommands(msg.getTurn(), msg.getPlayerCommands());
		});

		client.addListener(listener);

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					client.connect(6000, ip, port);
				} catch (IOException e) {
					ex = e;
					e.printStackTrace();
				}
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						callback.onClientConnected(ex);
					}
				});
			}
		});
		t.start();
	}

	/**
	 * Disconnects the client.
	 */
	public void disconnect() {
		client.close();
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

	/**
	 * Sets the event handler for client events.
	 * 
	 * @param eventHandler
	 *            The new event handler.
	 */
	public void setEventHandler(ClientEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

}
