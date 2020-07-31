package de.gg.game.core;

import de.eskalon.commons.settings.EskalonSettings;

/**
 * Manages the actual game settings.
 */
public class GGSettings extends EskalonSettings {

	private static final String DISCORD_INTEGRATION = "discordIntegration";

	public GGSettings(String fileName) {
		super(fileName);
	}

	// Discord Integration
	public boolean isDiscordIntegration() {
		return preferences.getBoolean(DISCORD_INTEGRATION, false);
	}

	public void setDiscordIntegration(boolean discordIntegration) {
		preferences.putBoolean(DISCORD_INTEGRATION, discordIntegration);
	}

}
