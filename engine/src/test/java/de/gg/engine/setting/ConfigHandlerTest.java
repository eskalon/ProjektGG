package de.gg.engine.setting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.gg.LibgdxUnitTest;

/**
 * Tests the {@link ConfigHandler} class.
 */
public class ConfigHandlerTest extends LibgdxUnitTest {

	/*
	 * @BeforeEach public void beforeEach() { // delete created preferences
	 * String path = System.getProperty("user.home") + "\\.prefs\\XYZ"; File
	 * file = new File(path); if (!file.delete()) { System.err.println(
	 * "Couldn't delete preferences. Test does not substantially cover the code."
	 * ); } }
	 */

	@Test
	public void testNPE() {
		assertThrows(NullPointerException.class, () -> {
			new ConfigHandler(null);
		});
	}

	@Test
	public void testIAE() {
		assertThrows(IllegalArgumentException.class, () -> {
			new ConfigHandler("");
		});
	}

	@Test
	public void testBasics() {
		final float floatValue = 2;
		final int intValue = 3;
		final String stringValue = "4";

		final String floatKey = "a";
		final String intKey = "b";
		final String stringKey = "c";

		ConfigHandler cfg = new ConfigHandler("XYZ");
		cfg.setFloat(floatKey, floatValue);
		assertTrue(cfg.hasValue(floatKey));
		cfg.setInt(intKey, intValue);
		cfg.setString(stringKey, stringValue);

		assertEquals(floatValue, cfg.getFloat(floatKey));
		assertEquals(intValue, cfg.getInt(intKey));
		assertEquals(stringValue, cfg.getString(stringKey));

		cfg.addToFloat(floatKey, floatValue);
		cfg.addToInt(intKey, intValue);

		assertEquals(floatValue * 2, cfg.getFloat(floatKey), 0.001F);
		assertEquals(intValue * 2, cfg.getInt(intKey));

	}

	/**
	 * Checks the ConfigHandler with existing keys
	 */
	@Test
	public void testDefaults() {
		final float floatValue = 2;
		final int intValue = 3;
		final String stringValue = "4";

		final String floatKey = "aa";
		final String intKey = "bb";
		final String stringKey = "cc";

		ConfigHandler cfg = new ConfigHandler("XYZ");

		assertEquals(floatValue, cfg.getFloat(floatKey, floatValue));
		assertEquals(intValue, cfg.getInt(intKey, intValue));
		assertEquals(stringValue, cfg.getString(stringKey, stringValue));

		assertEquals(floatValue, cfg.getFloat(floatKey, floatValue * 2));
		assertEquals(intValue, cfg.getInt(intKey, intValue * 2));
		assertEquals(stringValue,
				cfg.getString(stringKey, stringValue + "abc"));
	}

	/**
	 * Tests the ConfigHandler without existing keys.
	 */
	@Test
	public void testWithoutValues() {
		final float floatValue = 2;
		final int intValue = 3;
		final String stringValue = "4";

		final String floatKey = "aa";
		final String intKey = "bb";
		final String stringKey = "cc";

		ConfigHandler cfg = new ConfigHandler("ABC");

		assertEquals(floatValue, cfg.getFloat(floatKey, floatValue));
		assertEquals(intValue, cfg.getInt(intKey, intValue));
		assertEquals(stringValue, cfg.getString(stringKey, stringValue));

		assertEquals(floatValue, cfg.getFloat(floatKey, floatValue * 2));
		assertEquals(intValue, cfg.getInt(intKey, intValue * 2));
		assertEquals(stringValue,
				cfg.getString(stringKey, stringValue + "abc"));
	}
}
