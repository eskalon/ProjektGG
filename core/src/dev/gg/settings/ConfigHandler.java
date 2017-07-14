package dev.gg.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * A basic configuration handler. Takes care of reading from and writing to the
 * actual files. Encapsulates {@link Preferences}.
 */
public class ConfigHandler {

	private Preferences prefs;

	public ConfigHandler(String name) {
		prefs = Gdx.app.getPreferences(name);
	}

	/**
	 * @param key
	 *            The preference key.
	 * @return Whether the key was added to the preferences.
	 */
	public boolean hasValue(String key) {
		return prefs.contains(key);
	}

	// Integer
	/**
	 * Looks up an integer value and sets it to the default value if it's not
	 * existing.
	 * 
	 * @param key
	 *            The preference key.
	 * @param defaultValue
	 *            The default value.
	 * @return The value. If not existing the default value.
	 */
	public int getInt(String key, int defaultValue) {
		if (prefs.contains(key))
			return prefs.getInteger(key);

		prefs.putInteger(key, defaultValue);
		System.out.println("Key \"" + key + "\" nicht vorhanden!");
		return defaultValue;
	}

	/**
	 * Looks up an integer value and sets it to 0 if it's not existing.
	 * 
	 * @param key
	 *            The preference key.
	 * @return The value. If not existing 0.
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}

	/**
	 * Sets a preference key to the given value.
	 * 
	 * @param key
	 *            The preference key.
	 * @param value
	 *            The value.
	 */
	public void setInt(String key, int value) {
		prefs.putInteger(key, value);
		prefs.flush();
	}

	/**
	 * Adds a given amount to a numeric preference.
	 * 
	 * @param key
	 *            The preference key.
	 * @param addition
	 *            The amount to add.
	 */
	public void addToInt(String key, int addition) {
		setInt(key, addition + getInt(key));
	}

	// Float
	/**
	 * Looks up a float value and sets it to the default value if it's not
	 * existing.
	 * 
	 * @param key
	 *            The preference key.
	 * @param defaultValue
	 *            The default value.
	 * @return The value. If not existing the default value.
	 */
	public float getFloat(String key, float defaultValue) {
		if (prefs.contains(key))
			return prefs.getFloat(key);

		System.out.println("Key \"" + key + "\" nicht vorhanden!");
		prefs.putFloat(key, defaultValue);
		return defaultValue;
	}

	/**
	 * Looks up a float value and sets it to 0 if it's not existing.
	 * 
	 * @param key
	 *            The preference key.
	 * @return The value. If not existing 0.
	 */
	public float getFloat(String key) {
		return getFloat(key, 0);
	}

	/**
	 * Sets a preference key to the given value.
	 * 
	 * @param key
	 *            The preference key.
	 * @param value
	 *            The value.
	 */
	public void setFloat(String key, float value) {
		prefs.putFloat(key, value);
		prefs.flush();
	}

	/**
	 * Adds a given amount to a numeric preference.
	 * 
	 * @param key
	 *            The preference key.
	 * @param addition
	 *            The amount to add.
	 */
	public void addFloat(String key, float addition) {
		setFloat(key, addition + getFloat(key));
	}

}
