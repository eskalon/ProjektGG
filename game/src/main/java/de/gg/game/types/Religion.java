package de.gg.game.types;

import de.gg.engine.lang.Localizable;

public enum Religion implements Localizable {
	CATHOLIC, ORTHODOX;

	@Override
	public String getUnlocalizedName() {
		return "type.religion." + this.name().toLowerCase() + ".name";
	}
}