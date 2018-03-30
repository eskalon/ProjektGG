package de.gg.setting;

/**
 * Manages the actual game settings. Encapsulates {@link ConfigHandler}.
 */
public class GameSettings {

	private final ConfigHandler configHandler;
	private final String MASTER_VOLUME = "masterVolume";
	private final String EFFECT_VOLUME = "effectVolume";
	private final String MUSIC_VOLUME = "musicVolume";

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

}
