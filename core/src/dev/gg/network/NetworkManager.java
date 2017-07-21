package dev.gg.network;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import dev.gg.callback.IClientCallback;
import dev.gg.callback.IHostCallback;
import dev.gg.core.GameSession.GameDifficulty;
import dev.gg.network.event.ClientEventHandler;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.GameSetupMessage;
import dev.gg.network.message.PlayerChangedMessage;
import dev.gg.network.message.PlayerJoinedMessage;
import dev.gg.network.message.PlayerLeftMessage;
import dev.gg.network.message.TurnCommandsMessage;

/**
 * This class takes care of the actual networking part without interest in the
 * game logic.
 */
public class NetworkManager {

	private GameServer serverHandler;
	private Client client;
	private MultiplayerSession session;
	private ClientEventHandler eventHandler;
	private IOException ex;

	public NetworkManager(MultiplayerSession session) {
		this.session = session;
	}

	public void fixedUpdate() {
		if (serverHandler != null)
			serverHandler.fixedUpdate();
	}

	/**
	 * Sets up a server and a client asynchronically. After it is finished the
	 * appropriate {@linkplain IHostCallback#onHostStarted(IOException) callback
	 * method} is invoked.
	 * 
	 * @param port
	 *            The used port.
	 * @param speed
	 *            The game speed.
	 * @param callback
	 *            The callback.
	 */
	public void setUpAsHost(int port, GameDifficulty difficulty,
			IHostCallback callback) {
		serverHandler = new GameServer(difficulty);

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					serverHandler.setUpServer(port);
				} catch (IOException e) {
					ex = e;
					e.printStackTrace();
				}

				if (ex == null) {
					setUpAsClient("localhost", port, new IClientCallback() {
						@Override
						public void onClientStarted(IOException e) {
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									callback.onHostStarted(e);
								}
							});
						}
					});
				} else {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							callback.onHostStarted(ex);
						}
					});
				}
			}
		});
		t.start();
	}

	/**
	 * Sets up a client asynchronically. After it is finished the appropriate
	 * {@linkplain IClientCallback#onClientStarted(IOException) callback method}
	 * is invoked.
	 * 
	 * @param ip
	 *            The servers ip.
	 * @param port
	 *            The servers port.
	 * @param callback
	 *            The callback.
	 */
	public void setUpAsClient(String ip, int port, IClientCallback callback) {
		client = new Client();
		client.start();

		NetworkRegisterer.registerClasses(client.getKryo());

		TypeListener listener = new TypeListener();
		listener.addTypeHandler(GameSetupMessage.class, (con, msg) -> {
			Gdx.app.log("Client",
					"Lobby beigetreten (ID: " + msg.getId() + ")");
			session.setUp(msg.getPlayers(), msg.getId(), msg.getSeed(),
					msg.getDifficulty());
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
		listener.addTypeHandler(TurnCommandsMessage.class, (con, msg) -> {
			session.addNewCommands(msg.getTurn(), msg.getPlayerCommands());
		});

		client.addListener(listener);

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					client.connect(8000, ip, port);
				} catch (IOException e) {
					ex = e;
					e.printStackTrace();
				}
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						callback.onClientStarted(ex);
					}
				});
			}
		});
		t.start();
	}

	/**
	 * Stops client and server.
	 */
	public void stop() {
		if (serverHandler != null) {
			serverHandler.stopGame();
		}

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
	 * @return Whether also a server is running in this network manager
	 *         instance.
	 */
	public boolean isHost() {
		return serverHandler != null;
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
