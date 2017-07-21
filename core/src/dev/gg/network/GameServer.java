package dev.gg.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ConnectionListener;
import com.esotericsoftware.kryonet.Listener.TypeListener;
import com.esotericsoftware.kryonet.Server;

import dev.gg.command.PlayerCommands;
import dev.gg.core.GameSession.GameDifficulty;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.GameSetupMessage;
import dev.gg.network.message.PlayerChangedMessage;
import dev.gg.network.message.PlayerJoinedMessage;
import dev.gg.network.message.PlayerLeftMessage;
import dev.gg.network.message.PlayerTurnMessage;
import dev.gg.network.message.TurnCommandsMessage;
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
	private long seed = System.currentTimeMillis();
	private HashMap<Short, Player> players;
	private HashMap<Short, Connection> connections;
	private GameDifficulty difficulty;
	/**
	 * All command messages, that ever got received by the server.
	 */
	private HashMap<Integer, List<PlayerTurnMessage>> playerCommands;
	private int currentTurn = 1;

	public GameServer(GameDifficulty difficulty) {
		this.difficulty = difficulty;
		this.players = new HashMap<>();
		this.connections = new HashMap<>();
	}

	/**
	 * Sets up the game server.
	 * 
	 * @param port
	 *            The used tcp port.
	 * @throws IOException
	 *             Thrown if anything goes wrong.
	 */
	public void setUpServer(int port) throws IOException {
		server = new Server();
		server.start();

		// Log.INFO();

		NetworkRegisterer.registerClasses(server.getKryo());

		server.bind(port);

		server.addListener(new ConnectionListener() {
			@Override
			public void connected(Connection con) {
				Gdx.app.log("Server", "Client connected");

				players.put(playersJoinedCount,
						PlayerUtils.getRandomPlayer(players.values()));
				connections.put(playersJoinedCount, con);

				// Inform the other clients
				server.sendToAllExceptTCP(con.getID(), new PlayerJoinedMessage(
						playersJoinedCount, players.get(playersJoinedCount)));

				con.sendTCP(new GameSetupMessage(players, difficulty,
						playersJoinedCount, seed));
				
				playersJoinedCount++;
			}

			@Override
			public void disconnected(Connection con) {
				System.out.println("** [Server] Player disconnected");
				short id = CollectionUtils.getKeyByValue(connections, con);

				server.sendToAllExceptTCP(con.getID(),
						new PlayerLeftMessage(id));

				connections.remove(id);
				players.remove(id);
			}
		});

		TypeListener typeListener = new TypeListener();
		// Chat Message
		typeListener.addTypeHandler(ChatMessageSentMessage.class,
				(con, msg) -> {
					server.sendToAllExceptTCP(con.getID(), msg);
				});
		// Player Changed Message
		typeListener.addTypeHandler(PlayerChangedMessage.class, (con, msg) -> {
			server.sendToAllExceptTCP(con.getID(), msg);
		});
		// Command
		typeListener.addTypeHandler(PlayerTurnMessage.class, (con, msg) -> {
			if (!playerCommands.containsKey(msg.getTurn())) {
				playerCommands.put(msg.getTurn(), new ArrayList<>());
			}
			playerCommands.get(msg.getTurn()).add(msg);
		});

		server.addListener(typeListener);
	}

	public void startNewGame() {

	}

	public void startExistingGame() {

	}

	public void play() {

	}

	/**
	 * Updates the game server after one turn. Needed to send the command
	 * messages at the right time.
	 */
	public void fixedUpdate() {
		if (playerCommands.containsKey(currentTurn + 1)) {
			if (playerCommands.get(currentTurn + 1).size() != players.size()) {
				// TODO Auf restliche Nachrichten warten
				Gdx.app.error("Server",
						"[ERROR] Not all player commands received for turn "
								+ (currentTurn + 1) + ". "
								+ (players.size() - playerCommands
										.get(currentTurn + 1).size())
								+ " more are needed!");
			} else {
				List<PlayerCommands> cmds = new ArrayList<>();
				for (PlayerTurnMessage m : playerCommands
						.get(currentTurn + 1)) {
					if (m.getCommands() != null)
						cmds.add(m.getCommands());
				}

				server.sendToAllTCP(
						new TurnCommandsMessage(cmds, currentTurn + 1));
			}
		} else {
			// TODO Auf restliche Nachrichten warten
			Gdx.app.error("Server",
					"[ERROR] No player commands received for turn "
							+ (currentTurn + 1));
		}

		currentTurn++;

		// if (currentTurn % 3200 == 0)
		// Endgame screen
	}

	/**
	 * Stops the game and saves it. Also takes care of disposing the server.
	 */
	public void stopGame() {
		// TODO save the game
		dispose();
	}

	private void dispose() {
		server.close();
	}

}
