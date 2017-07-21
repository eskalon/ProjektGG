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

import dev.gg.command.PlayerCommand;
import dev.gg.core.GameSession.GameDifficulty;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.GameSetupMessage;
import dev.gg.network.message.NewCommandMessage;
import dev.gg.network.message.PlayerChangedMessage;
import dev.gg.network.message.PlayerJoinedMessage;
import dev.gg.network.message.PlayerLeftMessage;
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
	private int playersJoinedCount = 0;
	private long seed = System.currentTimeMillis();
	private HashMap<Integer, Player> players;
	private HashMap<Integer, Connection> connections;
	private GameDifficulty difficulty;
	/**
	 * All commands, that ever got received by the server.
	 */
	private HashMap<Integer, List<PlayerCommand>> commands;

	private int turn;
	/**
	 * This time is used to calculate the {@linkplain #turn turns}. The server
	 * normally runs half a turn behind the clients.
	 */
	private float time = -0.1F;

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
				int id = CollectionUtils.getKeyByValue(connections, con);

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
		typeListener.addTypeHandler(NewCommandMessage.class, (con, msg) -> {
			if (turn <= msg.getCommand().getTurn()) {
				saveCommand(msg.getCommand());
			} else {
				Gdx.app.debug("Server", "[ERROR] Discarded command '"
						+ msg.getClass().getSimpleName() + "' by player "
						+ msg.getCommand().getSenderID() + " for turn "
						+ msg.getCommand().getTurn() + " (" + turn + ")");
			}
		});

		server.addListener(typeListener);
	}

	/**
	 * Saves a command message for execution in the appropriate turn.
	 * 
	 * @param command
	 *            The command.
	 */
	private void saveCommand(PlayerCommand command) {
		if (!commands.containsKey(command.getTurn())) {
			commands.put(command.getTurn(), new ArrayList<>());
		}

		commands.get(command.getTurn()).add(command);
	}

	public void startNewGame() {

	}

	public void startExistingGame() {

	}

	public void play() {

	}

	/**
	 * Updates the game server. Needed to send the command messages at the right
	 * time. The game server naturally runs half a {@linkplain #turn turn}
	 * behind the clients.
	 * 
	 * @param delta
	 *            The time delta.
	 */
	public void update(float delta) {
		time += delta;

		if (time >= 0.2F) {
			time -= 0.2F;
			turn++;

			if (commands.containsKey(turn))
				server.sendToAllTCP(
						new TurnCommandsMessage(commands.get(turn)));
		}
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
