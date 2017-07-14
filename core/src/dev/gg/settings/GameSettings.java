package dev.gg.settings;

/**
 * Manages the actual game settings. Encapsulates {@link ConfigHandler}.
 *
 */
public class GameSettings {

	private final ConfigHandler configHandler;

	/**
	 * @param fileName
	 *            The name of the preference file.
	 */
	public GameSettings(String fileName) {
		configHandler = new ConfigHandler(fileName);
	}

}
