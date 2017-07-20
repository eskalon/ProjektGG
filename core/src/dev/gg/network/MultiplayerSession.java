package dev.gg.network;

import java.util.HashMap;
import java.util.List;

import dev.gg.callback.IClientCallback;
import dev.gg.callback.IHostCallback;
import dev.gg.command.PlayerCommand;
import dev.gg.core.GameSession;
import dev.gg.network.event.ClientEventHandler;
import dev.gg.network.message.ChatMessageSentMessage;
import dev.gg.network.message.PlayerChangedMessage;

/**
 * This class handles anything related to a multiplayer game session.
 */
public class MultiplayerSession extends GameSession {

	private NetworkManager networkManager;
	/**
	 * The network ID of the local player.
	 */
	private int localId;
	private HashMap<Integer, Player> players;

	public MultiplayerSession() {
		super();
		networkManager = new NetworkManager(this);
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
	public void setUp(HashMap<Integer, Player> players, int networkID,
			long randomSeed, GameDifficulty difficulty) {
		this.players = players;
		this.localId = networkID;
		setUp(randomSeed, difficulty);
	}

	/**
	 * Adds new commands to get executed.
	 * 
	 * @param commands
	 *            A list of the commands.
	 */
	public void addNewCommands(List<PlayerCommand> commands) {
		commands.addAll(commands);
	}

	@Override
	public void preUpdate(float delta) {
		networkManager.update(delta);
	}

	/**
	 * Executes a players command by sending it to the server first. Takes care
	 * of filling in the current turn and the sender ID.
	 * 
	 * @param command
	 *            The command message.
	 */
	@Override
	public void executeNewCommand(PlayerCommand command) {
		command.setTurn(turn + 2);
		command.setSenderID(localId);

		networkManager.sendObject(command);
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
	public HashMap<Integer, Player> getPlayers() {
		return players;
	}

	/**
	 * @return The local player's ID.
	 */
	public int getLocalID() {
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
