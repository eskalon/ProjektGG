package de.gg.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ConnectionListener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

import de.gg.data.GameSessionSetup;
import de.gg.game.AuthoritativeResultListener;
import de.gg.game.AuthoritativeSession;
import de.gg.game.SlaveActionListener;
import de.gg.network.message.ChatMessageSentMessage;
import de.gg.network.message.GameSetupMessage;
import de.gg.network.message.PlayerChangedMessage;
import de.gg.network.message.PlayerJoinedMessage;
import de.gg.network.message.PlayerLeftMessage;
import de.gg.util.CollectionUtils;
import de.gg.util.Log;
import de.gg.util.PlayerUtils;

public class GameServer {

	private Server server;
	private GameSessionSetup setup;
	private AuthoritativeSession session;
	private String gameName;
	/**
	 * A count of all joined players. Used to generate the player IDs.
	 */
	private short playersJoinedCount = 0;
	private HashMap<Short, LobbyPlayer> players;
	private HashMap<Short, Connection> connections;

	public GameServer(int port, String gameName, GameSessionSetup setup,
			IHostCallback callback) {
		players = new HashMap<>();
		connections = new HashMap<>();
		this.gameName = gameName;

		server = new Server();
		server.start();

		NetworkRegisterer.registerClasses(server.getKryo());

		// ON NEW CONNECTION & ON DICONNECTED
		server.addListener(new ConnectionListener() {
			@Override
			public void connected(Connection con) {
				onNewConnection(con);
			}

			@Override
			public void disconnected(Connection con) {
				onDisconnect(con);
			}
		});
		TypeListener typeListener = new TypeListener();
		// CHAT MESSAGE
		typeListener.addTypeHandler(ChatMessageSentMessage.class,
				(con, msg) -> {
					server.sendToAllExceptTCP(con.getID(), msg);
				}

		);
		// PLAYER CHANGED
		typeListener.addTypeHandler(PlayerChangedMessage.class,
				(con, msg) -> onPlayerChange(con, msg));

		server.addListener(typeListener);

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					server.bind(port);
				} catch (IOException e) {
					callback.onHostStarted(e);
					e.printStackTrace();
					return;

				}
				callback.onHostStarted(null);
			}
		});
		t.start();
	}

	/**
	 * Stops the server. Also takes care of saving the game.
	 */
	public void stop() {
		if (session != null)
			session.stopGame();
		server.stop();
	}

	// LISTENER METHDOS
	// ON NEW CONNECTION
	private synchronized void onNewConnection(Connection con) {
		Log.info("Server", "Client connected");

		LobbyPlayer p = PlayerUtils.getRandomPlayer(players.values());
		players.put(playersJoinedCount, p);
		connections.put(playersJoinedCount, con);

		// Inform the other clients
		server.sendToAllExceptTCP(con.getID(), new PlayerJoinedMessage(
				playersJoinedCount, players.get(playersJoinedCount)));

		// Send the setup response
		con.sendTCP(new GameSetupMessage(players, playersJoinedCount, setup));

		playersJoinedCount++;
	}

	// ON DISCONNECT
	private synchronized void onDisconnect(Connection con) {
		short id = CollectionUtils.getKeyByValue(connections, con);
		Log.info("Server", "Player %d disconnected", id);

		server.sendToAllExceptTCP(con.getID(), new PlayerLeftMessage(id));

		connections.remove(id);
		players.remove(id);
	}

	// ON PLAYER CHANGE
	private void onPlayerChange(Connection con, PlayerChangedMessage msg) {
		server.sendToAllExceptTCP(con.getID(), msg);

		players.put(msg.getId(), msg.getPlayer());

		System.out.println("Player changed " + msg.getId());

		if (PlayerUtils.areAllPlayersReady(players.values())) {
			session = new AuthoritativeSession(setup, players);

			// Register the RMI handler
			HashMap<Short, AuthoritativeResultListener> resultListeners = new HashMap<>();
			ObjectSpace.registerClasses(server.getKryo());
			ObjectSpace objectSpace = new ObjectSpace();
			objectSpace.register(254, (SlaveActionListener) session);

			for (Entry<Short, Connection> e : connections.entrySet()) {
				objectSpace.addConnection(e.getValue());

				AuthoritativeResultListener resultListener = ObjectSpace
						.getRemoteObject(e.getValue(), e.getKey(),
								AuthoritativeResultListener.class);
				resultListeners.put(e.getKey(), resultListener);

				if (resultListener == null)
					Log.error("Server",
							"Der resultListener des Spielers %d ist null",
							e.getKey());

			}

			session.startGame(resultListeners);

			Log.info("Server", "Spiel gestartet");
		}
	}

	interface IHostCallback {
		public void onHostStarted(IOException e);
	}

}
