package de.gg.util;

/**
 * A simple countdown timer.
 * <p>
 * After it was {@linkplain #start(int) started} it has to get
 * {@linkplain #update() updated}. After the specified target time is reached
 * the {@link #update(int)}-methods return <code>true</code>. To reuse the timer
 * is has to get {@linkplain #reset() reset} and {@linkplain #start(int)
 * started} again.
 */
public class CountdownTimer {

	private long lastTime = -1;
	private long timeRunning;
	/**
	 * The time after which the {@link #update(int)}-methods return
	 * <code>true</code> in milliseconds.
	 */
	private int targetTime;

	public void start(int targetTime) {
		this.targetTime = targetTime;
		this.timeRunning = 0;
		this.lastTime = System.currentTimeMillis();
	}

	public boolean update(int deltaMultiplier) {
		if (!isRunning())
			throw new IllegalStateException(
					"Timer muss zuerst gestartet werden!");

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

	public boolean isRunning() {
		return lastTime != -1;
	}

}
