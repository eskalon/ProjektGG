package de.gg.engine.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CountdownTimerTest {

	private CountdownTimer c;

	@Test
	public void test() {
		c = new CountdownTimer();

		assertThrows(IllegalStateException.class, () -> {
			c.update();
		});

		long startTime = System.currentTimeMillis();
		c.start(500);

		do {
			// wait
			assertTrue(c.isRunning());
		} while (!c.update());

		long duration = System.currentTimeMillis() - startTime;

		assertEquals(duration, 501); // timer stops, when limit is exceeded

		assertTrue(c.isRunning());
		c.reset();
		assertTrue(!c.isRunning());
	}

}
