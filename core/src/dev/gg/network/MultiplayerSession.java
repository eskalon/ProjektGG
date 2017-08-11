package dev.gg.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;

import dev.gg.callback.IClientCallback;
import dev.gg.callback.IHostCallback;
import dev.gg.command.PlayerCommand;
import dev.gg.core.GameSession;
import dev.gg.core.Player;
import dev.gg.data.GameSettings;
import dev.gg.network.event.ClientEventHandler;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.ClientTurnMessage;
import dev.gg.network.message.PlayerChangedMessage;

/**
 * This class handles anything related to a multiplayer game session.
 */
public class MultiplayerSession extends GameSession {

	private ClientNetworkHandler client;
	private GameServer server;
	/**
	 * The network ID of the local player.
	 */
	private short localId;
	private HashMap<Short, Player> players;
	private List<PlayerCommand> commandsForCurrentTurn;
	/**
	 * A hashmap of all the player commands scheduled for execution.
	 */
	private HashMap<Integer, HashMap<Short, List<PlayerCommand>>> serverCommands;

	public MultiplayerSession() {
		super();
		this.client = new ClientNetworkHandler(this);
		this.commandsForCurrentTurn = new ArrayList<>();
		this.serverCommands = new HashMap<>();
		this.client = new ClientNetworkHandler(this);
	}

	/**
	 * Sets up the game.
	 * 
	 * @param players
	 *            A hashmap containing the players.
	 * @param networkID
	 *            The networkID of the local player.
	 * @param randomSeed
	 *            The used random seed.
	 * @param difficulty
	 *            The game's difficulty.
	 */
	public void init(HashMap<Short, Player> players, short networkID,
			GameSettings settings) {
		super.init(settings);
		this.players = players;
		this.localId = networkID;
	}

	/**
	 * Adds new commands to get executed.
	 * 
	 * @param turn
	 *            The turn in which the commands should get executed.
	 * @param playerCommands
	 *            The commands.
	 */
	public synchronized void addNewCommands(int turn,
			HashMap<Short, List<PlayerCommand>> playerCommands) {
		this.serverCommands.put(turn, playerCommands);
	}

	@Override
	protected synchronized boolean processCommands(int turn) {
		if (serverCommands.containsKey(turn)) {
			if (serverCommands.get(turn) != null) {
				for (Entry<Short, List<PlayerCommand>> e : serverCommands
						.get(turn).entrySet()) {
					if (e.getValue() != null) {
						for (PlayerCommand c2 : e.getValue())
							processCommand(c2, e.getKey());
					}
				}
			}
			serverCommands.remove(turn);

			return true;
		} else {
			Gdx.app.error("Client",
					"[ERROR] The client did not receive the necessary command message for turn "
							+ currentTurn);
			if (currentTurn > 3) { // BUG should be '2' but the server doesn't
									// send a turn message for turn 3!
				return false;
			}
			return true; // only for the first two turns, for which no client
							// command messages could be sent
		}
	}

	public synchronized void sendCommands(int turn) {
		ClientTurnMessage message = new ClientTurnMessage(
				(commandsForCurrentTurn.isEmpty()
						? null
						: commandsForCurrentTurn),
				turn, localId);
		client.sendObject(message);
		System.out.println("[Client] Commands sent for turn " + (turn));
		commandsForCurrentTurn.clear();
	}

	/**
	 * {@inheritDoc} Has to be called after the client is
	 * {@linkplain # connected}.
	 */
	@Override
	public void start() {
		super.start();
	}

	@Override
	public synchronized void fixedUpdate() {
		sendCommands(currentTurn + 2);
	}

	/**
	 * Executes a players command by sending it to the server first.
	 * 
	 * @param command
	 *            The command message.
	 */
	@Override
	public void executeNewCommand(PlayerCommand command) {
		commandsForCurrentTurn.add(command);
	}

	/**
	 * Stops the client and if hosting the server as well.
	 */
	@Override
	public void stop() {
		client.disconnect();

		if (server != null)
			server.stop();
	}

	private Exception exxx;

	/**
	 * Sets up a server and a client asynchronically. After it is finished the
	 * appropriate {@linkplain IHostCallback#onHostStarted(IOException) callback
	 * method} is invoked.
	 * 
	 * @param port
	 *            The used port.
	 * @param settings
	 *            The game's settings.
	 * @param callback
	 *            The callback.
	 */
	public void setUpAsHost(int port, GameSettings settings,
			IHostCallback callback) {
		server = new GameServer(settings);

		server.start(port, new IHostCallback() {
			@Override
			public void onHostStarted(IOException e) {
				if (e == null) {
					setUpAsClient("localhost", port, new IClientCallback() {
						@Override
						public void onClientConnected(IOException e) {
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
							callback.onHostStarted(e);
						}
					});
				}
			}
		});
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
		client.connect(ip, port, callback);
	}

	/**
	 * @return The local player.
	 */
	@Override
	public Player getPlayer() {
		return players.get(localId);
	}

	/**
	 * Returns a hashmap of all players. The key is the player ID, the value the
	 * player object.
	 * 
	 * @return The player hashmap.
	 */
	public HashMap<Short, Player> getPlayers() {
		return players;
	}

	/**
	 * @return The local player's ID.
	 */
	public short getLocalID() {
		return localId;
	}

	/**
	 * @return Whether this player is also hosting the server.
	 */
	public boolean isHost() {
		return server != null;
	}

	public void setClientEventHandler(ClientEventHandler eventHandler) {
		client.setEventHandler(eventHandler);
	}

	/**
	 * Sends a chat message to the server.
	 * 
	 * @param chatMessage
	 *            The message.
	 */
	public void sendNewChatMessage(String chatMessage) {
		client.sendObject(new ChatMessageSentMessage(localId, chatMessage));
	}

	/**
	 * Updates the player on the server.
	 */
	public void onLocalPlayerChange() {
		client.sendObject(new PlayerChangedMessage(localId, getPlayer()));
	}

}
