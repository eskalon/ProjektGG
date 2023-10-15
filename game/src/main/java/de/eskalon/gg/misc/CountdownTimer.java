package de.eskalon.gg.misc;

import de.damios.guacamole.Preconditions;

/**
 * A simple countdown timer.
 * <p>
 * After it was {@linkplain #start(int) started} it has to get
 * {@linkplain #update() updated}. After the specified target time is reached
 * the {@link #update(int)}-methods return {@code true}. To reuse the timer is
 * has to get {@linkplain #reset() reset} and {@linkplain #start(int) started}
 * again.
 */
public class CountdownTimer {

	private long lastTime = -1;
	private long timeRunning;
	/**
	 * The time after which the {@link #update(int)}-methods return {@code true}
	 * in milliseconds.
	 */
	private int targetTime;

	public void start(int targetTime) {
		this.targetTime = targetTime;
		this.timeRunning = 0;
		this.lastTime = System.currentTimeMillis();
	}

	public boolean update(int deltaMultiplier) {
		Preconditions.checkState(isRunning(),
				"The timer has to get started first!");

		long currentTime = System.currentTimeMillis();
		timeRunning += (currentTime - lastTime) * deltaMultiplier;
		lastTime = currentTime;

		return timeRunning > targetTime;
	}

	public boolean update() {
		return update(1);
	}

	public void reset() {
		lastTime = -1;
	}

	/**
	 * @return whether the countdown timer is running. Is {@code true} even
	 *         after the target time is reached, as long as the timer is not
	 *         {@linkplain #reset() reset}.
	 */
	public boolean isRunning() {
		return lastTime != -1;
	}

}
