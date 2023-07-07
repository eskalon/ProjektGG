package de.gg.game.misc;

public class TickCounter {

	private TickHandler tickHandler;
	private int maxTicks, tickDuration;

	private boolean finished = false;
	private long lastTime = -1;
	private long updateTime;

	private int tickCount;

	/**
	 * Creates a tick counter.
	 *
	 * @param tickHandler
	 * @param maxTicks
	 *            the maximum number of ticks; the counter can be reset via
	 *            {@link #reset()}.
	 * @param tickDuration
	 *            the duration of a tick in
	 *            <code>milliseconds * {@link TickHandler#getDeltaMultiplier()}</code>
	 */
	public TickCounter(TickHandler tickHandler, int maxTicks,
			int tickDuration) {
		this(tickHandler, maxTicks, tickDuration, 0);
	}

	/**
	 * Creates a tick counter.
	 *
	 * @param tickHandler
	 * @param maxTicks
	 *            the maximum number of ticks; the counter can be reset via
	 *            {@link #reset()}.
	 * @param tickDuration
	 *            the duration of a tick in
	 *            <code>milliseconds * {@link TickHandler#getDeltaMultiplier()}</code>
	 * @param tickOffset
	 *            the initial offset of the {@linkplain #getTickCount() tick
	 *            count}
	 */
	public TickCounter(TickHandler tickHandler, int maxTicks, int tickDuration,
			int tickOffset) {
		this.tickHandler = tickHandler;
		this.maxTicks = maxTicks;
		this.tickDuration = tickDuration;
		this.tickCount = tickOffset;
	}

	/**
	 * Updates the tick counter. Whenever a tick is over,
	 * {@link TickHandler#onTick()} is called on the tick handler.
	 *
	 * @return Returns <code>true</code> <i>once</i>, when the max tick count is
	 *         reached. After that {@link #reset()} has to be called.
	 */
	public boolean update() {
		if (lastTime == -1)
			lastTime = System.currentTimeMillis();

		// Calculate delta
		long currentTime = System.currentTimeMillis();
		long delta = (currentTime - lastTime)
				* tickHandler.getDeltaMultiplier();
		lastTime = currentTime;

		updateTime += delta;

		// Calculate ticks
		while (updateTime >= tickDuration && tickCount < maxTicks) {
			// new update tick
			updateTime -= tickDuration;
			tickCount++;
			tickHandler.onTick();
		}

		// return true once, when maxTicks is reached
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

	/**
	 * @return the tick count; never more than the maximum.
	 */
	public int getTickCount() {
		return tickCount;
	}

	public interface TickHandler {
		public void onTick();

		public int getDeltaMultiplier();
	}

	/**
	 * Returns whether the specified interval is over and an action should get
	 * executed.
	 *
	 * @param tickInterval
	 *            the interval after which this method is supposed to return
	 *            true
	 * @return {@code true} when the specified tick interval is over
	 */
	public boolean isRightTick(int tickInterval) {
		return tickCount % tickInterval == 0;
	}

}
