package de.gg.game.model.types;

public enum PlayerIcon {
	ICON_1("72A0C1FF", "shield_light_blue"), ICON_2("9F2B68FF",
			"shield_pink"), ICON_3("FFBF00FF", "shield_yellow"), ICON_4(
					"4B5B0BFF", "shield_green"), ICON_5("4B0B0BFF",
							"shield_dark_red"), ICON_6("21219EFF",
									"shield_dark_blue"), ICON_7("4B0B61FF",
											"shield_purple");

	private String color;
	private String iconFileName;

	private PlayerIcon(String color, String iconFileName) {
		this.color = color;
		this.iconFileName = iconFileName;
	}

	/**
	 * @return the (chat) color for the player with this icon.
	 */
	public String getColor() {
		return color;
	}

	public String getIconDrawableName() {
		return "icon_" + iconFileName;
	}

	public String getShieldDrawableName() {
		return iconFileName;
	}
}