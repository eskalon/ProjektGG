package de.gg.game;

import de.gg.data.GameSessionSetup;
import de.gg.entity.City;
import de.gg.util.StoppableRunnable;

/**
 * This class holds the game data and takes care of calculating when a round
 * ends.
 */
public abstract class GameSession {

	private static final long ROUND_DURATION = 20 * 1000; // 8*60*1000
	protected volatile boolean waitingForNextRound = false;
	private GameSessionSetup setup;
	/**
	 * Used to calculate the time delta.
	 */
	private long lastTime = System.currentTimeMillis();
	/**
	 * Used to determine when the current round ends and the end round screen is
	 * shown. To start the following round, the server needs to receive every
	 * player's approval.
	 */
	private long currentRoundTime = ROUND_DURATION;
	private int currentRound = 0;

	private City city;

	/**
	 * Creates a new game session.
	 * 
	 * @param sessionSetup
	 *            The settings of the game session.
	 */
	public GameSession(GameSessionSetup sessionSetup) {
		this.setup = sessionSetup;
		this.city = new City();

		// TODO mit Hilfe des sessionSetup das Spiel aufsetzen, d.h. die
		// Spielwelt in #city einrichten
	}

	/**
	 * Updates the game session.
	 * 
	 * @return Whether the ingame day is over (8 minutes).
	 */
	public synchronized boolean update() {
		long currentTime = System.currentTimeMillis();
		long delta = currentTime - lastTime;
		lastTime = currentTime;

		currentRoundTime += delta;

		if (currentRoundTime < ROUND_DURATION) {
			update(delta);
		}

		return currentRoundTime >= ROUND_DURATION;
	}

	/**
	 * This method can be used to process the game.
	 * 
	 * @param delta
	 *            The time delta in milliseconds.
	 */
	public void update(long delta) {
	}

	/**
	 * Called after a round ended to start the next round.
	 */
	protected synchronized void startNextRound() {
		currentRound++;
		currentRoundTime = 0;
		waitingForNextRound = false;
	}

	/**
	 * @return The current round.
	 */
	public int getCurrentRound() {
		return currentRound;
	}

	/**
	 * @return The progress of the current round (in a range of 0 to 1). Can be
	 *         >1 right after a round ended and before the next round started.
	 */
	public float getRoundProgress() {
		return currentRoundTime / (float) ROUND_DURATION;
	}

	/**
	 * @return The game's setup.
	 */
	public GameSessionSetup getSetup() {
		return setup;
	}

	public City getCity() {
		return city;
	}

	/**
	 * This runnable updates a game session until a round is over. Once a round
	 * is over {@link #onRoundEnd()} is called. The updating resumes when
	 * {@link GameSession#startNextRound} got called.
	 */
	abstract class GameSessionUpdateRunnable extends StoppableRunnable {

		@Override
		protected synchronized void doStuff() {
			if (update()) {
				if (!waitingForNextRound) {
					waitingForNextRound = true;

					onRoundEnd();
				}
			}
		}

		/**
		 * This method is called once the current round is over.
		 */
		protected abstract void onRoundEnd();

	}

}
