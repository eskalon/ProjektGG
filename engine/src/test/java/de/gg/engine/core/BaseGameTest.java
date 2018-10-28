package de.gg.engine.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.eventbus.Subscribe;

import de.gg.LibgdxUnitTest;

public class BaseGameTest extends LibgdxUnitTest {

	@Test
	public void testConsturctor() {
		assertThrows(NullPointerException.class, () -> {
			new BaseGame(null, false) {
				@Override
				protected void onGameInitialization() {
				}
			};

		});
		assertThrows(IllegalArgumentException.class, () -> {
			new BaseGame("", false) {
				@Override
				protected void onGameInitialization() {
				}
			};

		});
		BaseGame game = new BaseGame("test-config", false) {
			@Override
			protected void onGameInitialization() {
			}
		};

		assertFalse(game.isInDebugMode());
		assertTrue(game.IN_DEV_ENV);
		assertEquals("Development", game.VERSION);
	}

	int k = 0;

	@Test
	public void testMembers() {
		BaseGame game = new BaseGame("test-config", false) {
			@Override
			protected void onGameInitialization() {
				k++;
			}
		};
		assertEquals(0, k);
		game.create();
		assertEquals(1, k);
		assertNotNull(game.getInputMultiplexer());
		assertNotNull(game.getAssetManager());
		assertNotNull(game.getSettings());
	}

	int i = 0;

	@Test
	public void testEventBus() {
		BaseGame game = new BaseGame("test-config", false) {
			@Override
			protected void onGameInitialization() {
				// TODO Auto-generated method stub

			}
		};
		game.create();

		assertNotNull(game.getEventBus());
		TestSubscriber sub = new TestSubscriber() {
			@Override
			public void test(TestEvent ev) {
				i++;
				assertEquals(43, ev.integer);
			}
		};
		TestEvent ev = new TestEvent();
		ev.integer = 43;
		game.getEventBus().register(sub);
		game.getEventBus().post(ev);

		assertEquals(0, i);

		game.getEventBus().distributeEvents();
		assertEquals(1, i);

		game.getEventBus().post(ev);
		assertEquals(1, i);
		game.render();
		assertEquals(2, i);
	}

	public abstract class TestSubscriber {
		@Subscribe
		public abstract void test(TestEvent ev);
	}

	public class TestEvent {
		public int integer;
	}

}
