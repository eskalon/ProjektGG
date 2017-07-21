package dev.gg.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;

import dev.gg.callback.IClientCallback;
import dev.gg.callback.IHostCallback;
import dev.gg.command.Command;
import dev.gg.command.PlayerCommands;
import dev.gg.core.GameSession;
import dev.gg.network.event.ClientEventHandler;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.PlayerChangedMessage;
import dev.gg.network.message.PlayerTurnMessage;

/**
 * This class handles anything related to a multiplayer game session.
 */
public class MultiplayerSession extends GameSession {

	/**
	 * A hashmap of all the player commands scheduled for execution.
	 */
	protected HashMap<Integer, List<PlayerCommands>> commands;
	private NetworkManager networkManager;
	/**
	 * The network ID of the local player.
	 */
	private short localId;
	private HashMap<Short, Player> players;
	private List<Command> commandsForCurrentTurn;

	public MultiplayerSession() {
		super();
		this.networkManager = new NetworkManager(this);
		this.commandsForCurrentTurn = new ArrayList<>();
		this.commands = new HashMap<>();
	}

	/**
	 * Sets up the game.
	 * 
	 * @param players
	 *            A hashmap with the players.
	 * @param networkID
	 *            The networkID of the local player.
	 * @param randomSeed
	 *            The used random seed.
	 * @param difficulty
	 *            The game's difficulty.
	 */
	public void setUp(HashMap<Short, Player> players, short networkID,
			long randomSeed, GameDifficulty difficulty) {
		this.players = players;
		this.localId = networkID;
		setUp(randomSeed, difficulty);
	}

	/**
	 * Adds new commands to get executed.
	 * 
	 * @param commands
	 *            The commands.
	 */
	public void addNewCommands(int turn, List<PlayerCommands> playerCommands) {
		commands.put(turn, playerCommands);
	}

	@Override
	protected void processCommands(int turn) {
		if (commands.containsKey(turn)) {
			for (PlayerCommands c : commands.get(turn)) {
				for (Command c2 : c.getCommands())
					processCommand(c2, c.getPlayerID());
			}
		} else {
			Gdx.app.error("Client",
					"[ERROR] The client did not receive the necessary command message for turn "
							+ currentTurn);
			// TODO Spiel pausieren, bis TurnCommandsMessage empfangen wird
		}
	}

	@Override
	public void onFixedUpdate() {
		PlayerTurnMessage message = new PlayerTurnMessage(
				commandsForCurrentTurn.isEmpty()
						? null
						: (new PlayerCommands(commandsForCurrentTurn, localId)),
				currentTurn + 2, localId);
		networkManager.sendObject(message);
		commandsForCurrentTurn.clear();
		networkManager.fixedUpdate();
	}

	/**
	 * Executes a players command by sending it to the server first.
	 * 
	 * @param command
	 *            The command message.
	 */
	@Override
	public void executeNewCommand(Command command) {
		commandsForCurrentTurn.add(command);
	}

	/**
	 * Stops the client and if hosting the server as well.
	 */
	@Override
	public void stop() {
		networkManager.stop();
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
		return networkManager.isHost();
	}

	public void setClientEventHandler(ClientEventHandler eventHandler) {
		networkManager.setEventHandler(eventHandler);
	}

	/**
	 * Sends a chat message to the server.
	 * 
	 * @param chatMessage
	 *            The message.
	 */
	public void sendNewChatMessage(String chatMessage) {
		networkManager
				.sendObject(new ChatMessageSentMessage(localId, chatMessage));
	}

	/**
	 * Updates the player on the server.
	 */
	public void onLocalPlayerChange() {
		networkManager
				.sendObject(new PlayerChangedMessage(localId, getPlayer()));
	}

	/**
	 * Sets up a server and a client asynchronically.
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
		networkManager.setUpAsHost(port, difficulty, callback);
	}

	/**
	 * Sets up a client asynchronically.
	 * 
	 * @param ip
	 *            The servers ip.
	 * @param port
	 *            The servers port.
	 * @param callback
	 *            The callback.
	 */
	public void setUpAsClient(String ip, int port, IClientCallback callback) {
		networkManager.setUpAsClient(ip, port, callback);
	}

}
