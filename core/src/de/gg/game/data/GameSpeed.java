package de.gg.game.data;

public enum GameSpeed {

	NORMAL(1), SPPED_2(2), SPPED_3(5), SPPED_4(10);

	private int deltaTimeMultiplied;

	GameSpeed(int deltaTimeMultiplied) {
		this.deltaTimeMultiplied = deltaTimeMultiplied;
	}

}
