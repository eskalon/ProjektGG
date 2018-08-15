package de.gg.util;

public class TickCounter {

	private TickHandler tickHandler;
	private int maxTicks, tickDuration;

	private boolean finished = false;
	private long lastTime = -1;
	private long updateTime;

	private int tickCount = 0;

	/**
	 * Creates a tick counter. Has to be {@linkplain #reset() reset} first to
	 * get used.
	 * 
	 * @param tickHandler
	 * @param maxTicks
	 *            The maximum number of ticks. The counter can be reset via
	 *            {@link #reset()}.
	 * @param tickDuration
	 *            The duration of a tick in
	 *            <code>milliseconds * {@link TickHandler#getDeltaMultiplier()}</code>.
	 */
	public TickCounter(TickHandler tickHandler, int maxTicks,
			int tickDuration) {
		this.tickHandler = tickHandler;
		this.maxTicks = maxTicks;
		this.tickDuration = tickDuration;

		tickCount = maxTicks;
	}

	/**
	 * Updates the tick counter. Whenever a tick is over,
	 * {@link TickHandler#onTick()} is called on the tick handler.
	 * 
	 * @return Returns <code>true</code> <i>once<i>, when the max tick count is
	 *         reached. After that {@link #reset()} has to be called.
	 */
	public boolean update() {
		if (lastTime == -1)
			lastTime = System.currentTimeMillis();

		// Zeit-Delta ermitteln
		long currentTime = System.currentTimeMillis();
		long delta = (currentTime - lastTime)
				* tickHandler.getDeltaMultiplier();
		lastTime = currentTime;

		updateTime += delta;

		// Ticks berechnen
		while (updateTime >= tickDuration && tickCount < maxTicks) {
			// Neuer Update Tick
			updateTime -= tickDuration;
			tickCount++;
			tickHandler.onTick();
		}

		// true einmalig bei maxTicks zurückgeben
		if (tickCount == maxTicks) {
			if (!finished) {
				finished = true;

				return true;
			}
		}

		return false;
	}

	/**
	 * Resets the tick counter. Is needed to start the
	 * {@linkplain TickHandler#onTick()} ticking again after the max tick count
	 * was reached.
	 */
	public void reset() {
		tickCount = 0;
		updateTime = 0;
		lastTime = -1;
		finished = false;
	}

	public int getTickCount() {
		return tickCount;
	}

	public interface TickHandler {
		public void onTick();

		public int getDeltaMultiplier();
	}

}
