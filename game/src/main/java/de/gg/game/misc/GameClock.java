package de.gg.game.misc;

import com.google.common.eventbus.EventBus;

import de.eskalon.commons.lang.Lang;
import de.gg.game.events.FullHourEvent;
import de.gg.game.session.GameSession;

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
	public void update() {
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
	public void resetClock() {
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
			season = Lang.get("clock.winter");
			break;
		}
		case 1: {
			season = Lang.get("clock.spring");
			break;
		}
		case 2: {
			season = Lang.get("clock.summer");
			break;
		}
		case 3: {
			season = Lang.get("clock.autumn");
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
