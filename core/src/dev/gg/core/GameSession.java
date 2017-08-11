package dev.gg.core;

import dev.gg.command.PlayerCommand;
import dev.gg.data.GameSettings;

/**
 * This class handles all the basic game stuff.
 */
public abstract class GameSession {

	private GameSettings settings;

	/**
	 * Used to calculate the time delta.
	 */
	private long lastTime = System.currentTimeMillis(), currentTime;
	/**
	 * Used to calculate when the {@linkplain #currentTurn current turn} ends.
	 */
	private long currentTurnTime;
	protected boolean isRunning = true;
	/**
	 * The current turn in game.
	 */
	protected int currentTurn = 1;
	private int TURN_DURATION = 2000;
	// unnecessary (?)
	private long pauseTime;

	public GameSession() {
	}

	/**
	 * Initializes the game session.
	 * 
	 * @param settings
	 *            The game session's settings.
	 */
	public void init(GameSettings settings) {
		this.settings = settings;
	}

	/**
	 * Updates the game session. Needed to process the command messages.
	 * 
	 * @return Whether the ingame day is over (8 minutes).
	 */
	public boolean update() {
		currentTime = System.currentTimeMillis();
		long delta = currentTime - lastTime;
		lastTime = currentTime;

		return update(delta);
	}

	protected boolean update(long delta) {
		// render() -> extern erledigen
		// updateSystems();

		if (isRunning)
			currentTurnTime += delta;
		else
			pauseTime += delta;

		if (currentTurnTime >= TURN_DURATION) {
			if (processCommands(currentTurn)) {
				isRunning = true;
			} else {
				isRunning = false;
				return false;
			}
			fixedUpdate();

			currentTurnTime -= TURN_DURATION;
			currentTurn++;

			if (currentTurn % 3200 == 0) // TODO calculate this in another way –
											// for example via the real time
				return true; // Switch to the EndgameScreen
		}

		return false;
	}

	/**
	 * Used to update things in each turn.
	 */
	protected void fixedUpdate() {
	}

	/**
	 * Processes all commands for the given turn. The processing is repeated
	 * until it succeeds.
	 * 
	 * @param turn
	 *            The turn.
	 * @return If the processing was successful.
	 * @see #processCommand(PlayerCommand)
	 */
	protected abstract boolean processCommands(int turn);

	/**
	 * Executes one command message.
	 * 
	 * @param c
	 *            The command message.
	 * @param id
	 *            The executing player's id.
	 */
	protected void processCommand(PlayerCommand c, short id) {
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
	public abstract void executeNewCommand(PlayerCommand command);

	/**
	 * Starts the actual game and therefore the processing. Has to be called
	 * after the {@linkplain #init(GameSettings) initialization}.
	 */
	public void start() {
		// TODO Set up the game

		GameSession session = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					session.update();
				}
			}
		}).start();

	}

	/**
	 * Stops the game and saves it.
	 */
	public void stop() {

	}

	/**
	 * @return The game's settings.
	 */
	public GameSettings getSettings() {
		return settings;
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
