package de.gg.engine.data;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.Color;

public class DataStoreTest {

	@Test
	public void testHandler() {
		final String key = "xyz";
		final Color value = new Color();

		DataStore store = new DataStore();
		assertTrue(!store.contains(key));

		store.put(key, value);
		assertTrue(store.contains(key));
		assertSame(value, store.get(key));
		assertSame(value, store.get(key, Color.class));

		assertNull(store.get("xya", String.class));

		assertThrows(NullPointerException.class, () -> {
			store.put(null, null);
		});
		assertThrows(NullPointerException.class, () -> {
			store.contains(null);
		});
		assertThrows(NullPointerException.class, () -> {
			store.get(null);
		});
		assertThrows(NullPointerException.class, () -> {
			store.put("abc", null);
		});
		assertThrows(NullPointerException.class, () -> {
			store.get(null, String.class);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			store.put("", null);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			store.contains("");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			store.get("");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			store.get("", String.class);
		});
	}

}
