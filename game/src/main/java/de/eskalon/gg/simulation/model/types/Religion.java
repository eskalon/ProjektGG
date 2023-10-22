package de.eskalon.gg.simulation.model.types;

import de.eskalon.commons.lang.ILocalizable;

public enum Religion implements ILocalizable {
	CATHOLIC, ORTHODOX;

	@Override
	public String getUnlocalizedName() {
		return "type.religion." + this.name().toLowerCase() + ".name";
	}

}