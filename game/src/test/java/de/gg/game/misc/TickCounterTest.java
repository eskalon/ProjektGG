package de.gg.game.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.gg.game.misc.TickCounter.TickHandler;

public class TickCounterTest {

	private TickCounter t;
	private int tickCount = 0;

	@Test
	public void testTickCounter() {
		int maxTicks = 75;
		int tickDuration = 30;
		int deltaMultiplier = 5;

		t = new TickCounter(new TickHandler() {
			@Override
			public void onTick() {
				tickCount++;
			}

			@Override
			public int getDeltaMultiplier() {
				return deltaMultiplier;
			}
		}, maxTicks, tickDuration);

		long startTime = System.currentTimeMillis();

		while (!t.update()) {
			// wait
		}

		long duration = System.currentTimeMillis() - startTime;

		// Normale Tests
		assertEquals(tickCount, t.getTickCount());
		assertEquals(maxTicks, t.getTickCount());
		assertEquals(maxTicks * tickDuration / deltaMultiplier, duration);

		// Keine weiteren Ticks nach max_tick
		assertEquals(false, t.update());
		assertEquals(tickCount, t.getTickCount());
		assertEquals(maxTicks, t.getTickCount());

		// Reset
		t.reset();
		tickCount = 0;
		while (!t.update()) {
			// wait
		}
		assertEquals(tickCount, t.getTickCount());
	}

}
