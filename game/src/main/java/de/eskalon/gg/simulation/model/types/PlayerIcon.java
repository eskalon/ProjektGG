package de.eskalon.gg.simulation.model.types;

import lombok.Getter;

public enum PlayerIcon {
	BLUE_LIGHT("72A0C1FF", "shield_light_blue"), PINK("9F2B68FF",
			"shield_pink"), YELLOW("FFBF00FF", "shield_yellow"), GREEN(
					"4B5B0BFF", "shield_green"), RED_DARK("4B0B0BFF",
							"shield_dark_red"), BLUE_DARK("21219EFF",
									"shield_dark_blue"), PURPLE("4B0B61FF",
											"shield_purple");

	/**
	 * The (chat) color for the player with this icon.
	 */
	private @Getter String color;
	private String iconFileName;

	private PlayerIcon(String color, String iconFileName) {
		this.color = color;
		this.iconFileName = iconFileName;
	}

	public String getIconDrawableName() {
		return "icon_" + iconFileName;
	}

	public String getShieldDrawableName() {
		return iconFileName;
	}
}