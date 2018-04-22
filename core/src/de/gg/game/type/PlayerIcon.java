package de.gg.game.type;

public enum PlayerIcon {
	ICON_1("72A0C1FF", "icon_1"), ICON_2("9F2B68FF",
			"icon_2"), ICON_3("FFBF00FF", "icon_1");

	private String color;
	private String iconFileName;

	private PlayerIcon(String color, String iconFileName) {
		this.color = color;
		this.iconFileName = iconFileName;
	}

	public String getColor() {
		return color;
	}

	public String getIconFileName() {
		return iconFileName;
	}

}