package dev.gg.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ConnectionListener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;

import dev.gg.callback.IHostCallback;
import dev.gg.command.PlayerCommand;
import dev.gg.core.Player;
import dev.gg.data.GameSettings;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.ClientTurnMessage;
import dev.gg.network.message.GameSetupMessage;
import dev.gg.network.message.PlayerChangedMessage;
import dev.gg.network.message.PlayerJoinedMessage;
import dev.gg.network.message.PlayerLeftMessage;
import dev.gg.network.message.SeverTurnMessage;
import dev.gg.util.CollectionUtils;
import dev.gg.util.PlayerUtils;

/**
 * This class represents a game server instance.
 */
public class GameServer {

	private Server server;
	/**
	 * A count of all joined players. Used to generate the player IDs.
	 */
	private short playersJoinedCount = 0;
	private GameSettings settings;
	private HashMap<Short, ServerPlayer> clients;
	private HashMap<Short, Player> players;
	private HashMap<Short, Connection> connections;

	/**
	 * This time is used to calculate the {@linkplain #currentTurn turns}.
	 */
	private long currentTurnTime;
	private long lastTime = System.currentTimeMillis(), currentTime;
	protected boolean isRunning = true;
	/**
	 * The current turn in game.
	 */
	protected int currentTurn = 1;
	private int TURN_DURATION = 2000;

	public GameServer(GameSettings settings) {
		this.settings = settings;
		this.clients = new HashMap<>();
		this.connections = new HashMap<>();
		this.players = new HashMap<>();
	}

	/**
	 * Starts the game server asynchronically. After it is finished the
	 * appropriate {@linkplain IHostCallback#onHostStarted(IOException) callback
	 * method} is invoked. The callback is <i>not</i> invoked on the rendering
	 * thread.
	 * 
	 * @param port
	 *            The used port.
	 * @param callback
	 *            The callback.
	 */
	public void start(int port, IHostCallback callback) {
		server = new Server();
		server.start();

		NetworkRegisterer.registerClasses(server.getKryo());

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
		// Chat Message
		typeListener.addTypeHandler(ChatMessageSentMessage.class,
				(con, msg) -> {
					server.sendToAllExceptTCP(con.getID(), msg);
				}

		);
		// Player Changed Message
		typeListener.addTypeHandler(PlayerChangedMessage.class,
				(con, msg) -> onPlayerChange(con, msg));
		// Command
		typeListener.addTypeHandler(ClientTurnMessage.class,
				(con, msg) -> onNewClientTurnMessage(con, msg));

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

	private void update() {
		currentTime = System.currentTimeMillis();
		long delta = currentTime - lastTime;
		lastTime = currentTime;

		update(delta);
	}

	private void update(long delta) {
		if (isRunning) {
			currentTurnTime += delta;
		}

		if (currentTurnTime >= TURN_DURATION) {
			// System.out.println("[Server] processing turn " + currentTurn);
			if (sendMessage(currentTurn + 1)) {
				isRunning = true;
			} else {
				isRunning = false;
				return;
			}

			currentTurn++;
			currentTurnTime -= TURN_DURATION;
		}

	}

	/**
	 * Updates the game server after one turn. Needed to send the command
	 * messages at the right time.
	 */
	public synchronized boolean sendMessage(int turn) {
		for (ServerPlayer s : clients.values()) {
			if (!s.isReadyForTurn(turn)) {
				Gdx.app.error("Server",
						"[ERROR] Not all player commands received for turn "
								+ turn);

				if (currentTurn > 2)
					return false;
				else
					return true;
			}
		}

		sendTurnResponse(turn);
		return true;
	}

	private synchronized void sendTurnResponse(int turn) {
		HashMap<Short, List<PlayerCommand>> cmds = new HashMap<>();
		for (Entry<Short, ServerPlayer> entry : clients.entrySet()) {
			if (entry.getValue().getMessageForTurn(turn).getCommands() != null)
				cmds.put(entry.getKey(),
						entry.getValue().getMessageForTurn(turn).getCommands());
		}

		server.sendToAllTCP(
				new SeverTurnMessage(cmds.size() == 0 ? null : cmds, turn));

		System.out.println("[Server] Turn response sent for turn " + turn);
	}

	/**
	 * Stops the game and saves it. Also takes care of disposing the server.
	 */
	public void stop() {
		// TODO save the game
		server.close();
	}

	// LISTENER METHDOS
	private synchronized void onNewConnection(Connection con) {
		Gdx.app.log("Server", "Client connected");

		Player p = PlayerUtils.getRandomPlayer(players.values());
		players.put(playersJoinedCount, p);
		clients.put(playersJoinedCount, new ServerPlayer(p));
		connections.put(playersJoinedCount, con);

		// Inform the other clients
		server.sendToAllExceptTCP(con.getID(), new PlayerJoinedMessage(
				playersJoinedCount, players.get(playersJoinedCount)));

		// Send the setup response
		con.sendTCP(
				new GameSetupMessage(players, playersJoinedCount, settings));

		playersJoinedCount++;
	}

	private synchronized void onDisconnect(Connection con) {
		short id = CollectionUtils.getKeyByValue(connections, con);
		Gdx.app.log("[Server]", "Player " + id + " disconnected");

		server.sendToAllExceptTCP(con.getID(), new PlayerLeftMessage(id));

		connections.remove(id);
		clients.remove(id);
		players.remove(id);
	}

	private void onPlayerChange(Connection con, PlayerChangedMessage msg) {
		server.sendToAllExceptTCP(con.getID(), msg);

		players.put(msg.getId(), msg.getPlayer());

		System.out.println("Player changed " + msg.getId());

		if (PlayerUtils.areAllPlayersReady(players.values())) {
			// TODO Spiel aufsetzen

			// Spiel starten
			GameServer server = this;
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						server.update();
					}
				}
			}).start();
		}
	}

	private synchronized void onNewClientTurnMessage(Connection con,
			ClientTurnMessage msg) {
		clients.get(msg.getClientID()).addMessage(msg.getTurn(), msg);
		System.out.println("[Server] Commands received for turn "
				+ msg.getTurn() + " by player " + msg.getClientID());
	}

}
