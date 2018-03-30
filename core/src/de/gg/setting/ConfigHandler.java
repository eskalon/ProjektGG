package de.gg.setting;

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

    // STRING
    /**
     * Looks up a String value and sets it to the default value if it's not
     * existing.
     * 
     * @param key
     *            The preference key.
     * @param defaultValue
     *            The default value.
     * @return The value. If not existing the default value.
     */
    public String getString(String key, String defaultValue) {
        if (prefs.contains(key))
            return prefs.getString(key);

        setString(key, defaultValue);

        return defaultValue;
    }

    /**
     * Looks up a String value and sets it to an empty String if it's not existing.
     * 
     * @param key
     *            The preference key.
     * @return The value. If not existing an empty String.
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * Sets a preference key to the given value.
     * 
     * @param key
     *            The preference key.
     * @param value
     *            The value.
     */
    public void setString(String key, String value) {
        prefs.putString(key, value);
        prefs.flush();
    }

    // INTEGER
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

        setInt(key, defaultValue);

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

    // FLOAT
    /**
     * Looks up a float value and sets it to the default value if it's not existing.
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

        setFloat(key, defaultValue);

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
    public void addToFloat(String key, float addition) {
        setFloat(key, addition + getFloat(key));
    }

}
