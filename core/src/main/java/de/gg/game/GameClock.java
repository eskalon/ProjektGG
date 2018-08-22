package de.gg.game;

import com.google.common.eventbus.EventBus;

import de.gg.event.FullHourEvent;

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
	 * The in-game minutes passing per clock update.
	 */
	private static final float MINUTES_PER_UPDATE = HOURS_PER_DAY * 60
			/ GameSession.ROUND_DURATION_IN_SECONDS;

	private float minute, hour;

	private EventBus eventBus;

	public GameClock(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * Should get called every ten ticks (= 1 second). Updates the clock.
	 */
	protected void update() {
		minute += MINUTES_PER_UPDATE;

		if (minute >= 60) {
			minute -= 60;
			hour++;

			eventBus.post(new FullHourEvent());
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
		return (int) hour;
	}

	public int getMinute() {
		return (int) minute;
	}

	/**
	 *
	 * @param round
	 *            The current game round.
	 * @return The season for the respective round.
	 */
	public static String getSeason(int round) {
		String season = null;
		switch (round % 4) {
		case 0: {
			season = "Winter";
			break;
		}
		case 1: {
			season = "Frühjahr";
			break;
		}
		case 2: {
			season = "Sommer";
			break;
		}
		case 3: {
			season = "Herbst";
			break;
		}
		}
		return season;
	}

	/**
	 *
	 * @param round
	 *            The game round.
	 * @return The year the game round takes place in.
	 */
	public static int getYear(int round) {
		return round + 1304;
	}

}
