package dev.gg.core;

import java.util.Deque;
import java.util.Random;

import dev.gg.command.PlayerCommand;
import dev.gg.data.DataStore;
import dev.gg.network.Player;
import dev.gg.network.message.NewCommandMessage;

/**
 * This class handles all the basic game stuff.
 */
public abstract class GameSession {

	/**
	 * A dequeue of all the current and future player commands. All child
	 * classes have to take care of filling it for themselves.
	 */
	protected Deque<PlayerCommand> commands;
	private GameDifficulty difficulty;
	private DataStore dataStore;
	private Random random;

	public GameSession() {
		this.dataStore = new DataStore();
	}

	/**
	 * Sets up the game session.
	 * 
	 * @param seed
	 *            The seed used by the random generator.
	 * @param difficulty
	 *            The game's difficulty.
	 */
	public void setUp(long seed, GameDifficulty difficulty) {
		this.difficulty = difficulty;
		this.random = new Random(seed);
	}

	/**
	 * This time is used to calculate the {@linkplain #turn turns}.
	 */
	// TODO use long
	private double time = 0;
	protected int turn = 0;

	/**
	 * Updates the game session. Needed to process the command messages.
	 * 
	 * @param delta
	 *            The time delta.
	 * @return Whether the ingame day is over (8 minutes).
	 */
	public boolean update(float delta) {
		preUpdate(delta);

		time += delta;

		if (time >= 0.2F) {
			time -= 0.2F;
			turn++;

			processCommands(turn);
			// TODO update the ECS (except renderer-systems)

			if (turn % 2400 == 0)
				return true; // Switch to the EndgameScreen
		}

		return false;

	}

	/**
	 * Used to update things before the actual updating takes place.
	 * 
	 * @param delta
	 *            The time delta.
	 */
	public void preUpdate(float delta) {
	}

	/**
	 * Executes all commands for the given turn.
	 * 
	 * @param turn
	 *            The turn.
	 * @see #executeCommand(NewCommandMessage)
	 */
	private void processCommands(int turn) {
		if (!commands.isEmpty()) {
			while (commands.peekFirst().getTurn() == turn) {
				processCommand(commands.pollFirst());
			}
		}
	}

	/**
	 * Executes one command message.
	 * 
	 * @param c
	 *            The command message.
	 */
	private void processCommand(PlayerCommand c) {
		// TODO
	}

	/**
	 * Called by the player when he does a thing.
	 * 
	 * @param command
	 *            The command issued by the player.
	 */
	public abstract void executeNewCommand(PlayerCommand command);

	/**
	 * Stops the game and saves it.
	 */
	public void stop() {

	}

	public void renderMap(float delta, int x, int y) {
		// TODO
	}

	public void renderHouse(float delta) {
		// TODO
	}

	/**
	 * @return The game data store.
	 */
	public DataStore getDataStore() {
		return this.dataStore;
	}

	/**
	 * @return The used random number generator.
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * @return The game's difficulty.
	 */
	public GameDifficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * @return The local player.
	 */
	public abstract Player getPlayer();

	/**
	 * An enum describing the game difficulty.
	 */
	public enum GameDifficulty {

		EASY, NORMAL, HARD;
	}

}
