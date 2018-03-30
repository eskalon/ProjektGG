package de.gg.core;

import de.gg.data.GameSessionSetup;
import de.gg.data.RoundEndData;
import de.gg.entity.City;

/**
 * This class holds the game data and takes care of calculating when a round
 * ends.
 */
public abstract class GameSession {

	private static final long ROUND_DURATION = 100;
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
	public boolean update() {
		long currentTime = System.currentTimeMillis();
		long delta = currentTime - lastTime;
		lastTime = currentTime;

		currentRoundTime += delta;

		return currentRoundTime > ROUND_DURATION;
	}

	/**
	 * Called after a round ended to setup the next round.
	 * 
	 * @param data
	 *            This data contains all calculations done after a round i.e. a
	 *            salary costs, tuition effects, etc.
	 */
	public void setupNewRound(RoundEndData data) {
		// TODO apply the round end data (salaries, etc.)

		currentRoundTime = 0;
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
