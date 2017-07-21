package dev.gg.core;

import java.util.Random;

import dev.gg.command.Command;
import dev.gg.data.DataStore;
import dev.gg.network.Player;

/**
 * This class handles all the basic game stuff.
 */
public abstract class GameSession {

	private GameDifficulty difficulty;
	private DataStore dataStore;
	private Random random;
	/**
	 * These times are used to calculate the time delta.
	 */
	private long lastTime = System.currentTimeMillis(), currentTime;
	/**
	 * This time is used to calculate the {@linkplain #currentTurn turns}.
	 */
	private long timeRunning;
	private boolean isRunning = true;
	/**
	 * The current turn in game.
	 */
	protected int currentTurn = 1;

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
	 * Updates the game session. Needed to process the command messages.
	 * 
	 * @return Whether the ingame day is over (8 minutes).
	 */
	public boolean update() {
		while (isRunning) {
			currentTime = System.currentTimeMillis();
			long delta = currentTime - lastTime;
			lastTime = currentTime;

			// TODO update the ECS (except renderer-systems)

			timeRunning += delta;

			if (timeRunning >= 150) {
				processCommands(currentTurn);

				onFixedUpdate();

				currentTurn++;
				timeRunning -= 150;

				if (currentTurn % 3200 == 0)
					return true; // Switch to the EndgameScreen
			}
		}

		return false;
	}

	/**
	 * Used to update things in each turn.
	 */
	public void onFixedUpdate() {
	}

	/**
	 * Processes all commands for the given turn.
	 * 
	 * @param turn
	 *            The turn.
	 * @see #processCommand(Command)
	 */
	protected abstract void processCommands(int turn);

	/**
	 * Executes one command message.
	 * 
	 * @param c
	 *            The command message.
	 * @param id
	 *            The executing player's id.
	 */
	protected void processCommand(Command c, int id) {
		// TODO
	}

	/**
	 * Called by the player when he does a thing.
	 * 
	 * @param command
	 *            The command issued by the player.
	 * 
	 * @see #processCommands(int)
	 */
	public abstract void executeNewCommand(Command command);

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
