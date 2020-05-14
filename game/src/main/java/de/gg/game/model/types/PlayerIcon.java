package de.gg.game.model.types;

public enum PlayerIcon {
	ICON_1("72A0C1FF", "gold_coin"), ICON_2("9F2B68FF",
			"gold_coin"), ICON_3("FFBF00FF", "gold_coin");

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

	public String getIconFileName() {
		return iconFileName;
	}
}