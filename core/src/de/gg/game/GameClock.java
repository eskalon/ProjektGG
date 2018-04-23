package de.gg.game;

/**
 * This class takes care of translating the update ticks to the in-game time.
 * Every ten ticks {@link #update()} has to get called.
 */
public class GameClock {

	/**
	 * The duration of an in-game day in in-game hours.
	 */
	private static final int HOURS_PER_DAY = 17;
	/**
	 * The hour the in-game day starts.
	 */
	private static final int STARTING_HOUR = 6;
	/**
	 * The in-game minutes passing per update.
	 */
	private static final float MINUTES_PER_UPDATE = HOURS_PER_DAY * 60
			/ (GameSession.ROUND_DURATION / 1000);

	private int minute, hour;

	/**
	 * Should get called every ten ticks (= 1 second). Updates the clock.
	 */
	protected void update() {
		minute += MINUTES_PER_UPDATE;

		if (minute >= 60) {
			minute -= 60;
			hour++;
		}
	}

	/**
	 * Resets the clock.
	 */
	protected void resetClock() {
		hour = STARTING_HOUR;
		minute = 0;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

}
