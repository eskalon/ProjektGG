package de.eskalon.gg.simulation;

import de.eskalon.commons.event.EventBus;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.gg.events.FullHourEvent;

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
			/ GameHandler.TICKS_PER_ROUND;

	private @Inject EventBus eventBus;

	private float minute, hour;
	private int round;

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

	public String getSeason() {
		switch (round % 4) {
		case 0:
			return Lang.get("clock.winter");
		case 1:
			return Lang.get("clock.spring");
		case 2:
			return Lang.get("clock.summer");
		case 3:
			return Lang.get("clock.autumn");
		}
		return Lang.get("ui.generic.error");
	}

	public int getYear() {
		return round + 1304;
	}

	public void setRound(int round) {
		this.round = round;
	}

}