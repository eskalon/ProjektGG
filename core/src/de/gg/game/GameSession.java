package de.gg.game;

import de.gg.data.GameSessionSetup;
import de.gg.entity.City;
import de.gg.util.Log;

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
	private volatile long currentRoundTime = ROUND_DURATION;
	private volatile int currentRound = 0;

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
	 * Updates the game session. Returns true once, when a round is over. To
	 * start the next round call {@link #startNextRound()}.
	 * 
	 * @return Whether the ingame day is over (8 minutes).
	 */
	public synchronized boolean update() {
		// Zeit-Delta ermitteln
		long currentTime = System.currentTimeMillis();
		long delta = currentTime - lastTime;
		lastTime = currentTime;

		// Rundenzeit hochzählen
		currentRoundTime += delta;

		if (currentRoundTime >= ROUND_DURATION) {
			// Runde zuende
			if (!waitingForNextRound) {
				waitingForNextRound = true;

				if (getRoundProgress() < 1)
					for (int i = 0; i < 6; i++)
						Log.error("Client", "Thread-Bug in Runde %s",
								getCurrentRound());

				return true;

			}
		} else {
			// Runde läuft noch
			update(delta);
		}

		return false;
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
		lastTime = System.currentTimeMillis();
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

}
