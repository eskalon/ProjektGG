package de.gg.setting;

import com.badlogic.gdx.Input.Keys;

/**
 * Manages the actual game settings. Encapsulates {@link ConfigHandler}.
 */
public class GameSettings {

	private final ConfigHandler configHandler;
	// Volume
	public static final String MASTER_VOLUME = "masterVolume";
	public static final String EFFECT_VOLUME = "effectVolume";
	public static final String MUSIC_VOLUME = "musicVolume";
	// Keys
	public static final String FORWARD_KEY = "forwardKey";
	public static final String LEFT_KEY = "leftKey";
	public static final String BACKWARD_KEY = "backwardKey";
	public static final String RIGHT_KEY = "rightKey";

	/**
	 * @param fileName
	 *            The name of the preference file.
	 */
	public GameSettings(String fileName) {
		configHandler = new ConfigHandler(fileName);
	}

	// Master volume
	public float getMasterVolume() {
		return configHandler.getFloat(MASTER_VOLUME, 1F);
	}

	public void setMasterVolume(float masterVolume) {
		configHandler.setFloat(MASTER_VOLUME, masterVolume);
	}

	// Effect volume
	public float getEffectVolume() {
		return configHandler.getFloat(EFFECT_VOLUME, 1F);
	}

	public void setEffectVolume(float effectVolume) {
		configHandler.setFloat(EFFECT_VOLUME, effectVolume);
	}

	// Music volume
	public float getMusicVolume() {
		return configHandler.getFloat(MUSIC_VOLUME, 1F);
	}

	public void setMusicVolume(float musicVolume) {
		configHandler.setFloat(MUSIC_VOLUME, musicVolume);
	}

	// Forward key
	public int getForwardKey() {
		return configHandler.getInt(FORWARD_KEY, Keys.W);
	}

	public void setForwardKey(int key) {
		configHandler.setInt(FORWARD_KEY, key);
	}

	// Left key
	public int getLeftKey() {
		return configHandler.getInt(LEFT_KEY, Keys.A);
	}

	public void setLeftKey(int key) {
		configHandler.setInt(LEFT_KEY, key);
	}

	// Backward key
	public int getBackwardKey() {
		return configHandler.getInt(BACKWARD_KEY, Keys.S);
	}

	public void setBackwardKey(int key) {
		configHandler.setInt(BACKWARD_KEY, key);
	}

	// Right key
	public int getRightKey() {
		return configHandler.getInt(RIGHT_KEY, Keys.D);
	}

	public void setRightKey(int key) {
		configHandler.setInt(RIGHT_KEY, key);
	}

}
