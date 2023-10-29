package de.eskalon.gg.simulation.model.entities;

import de.eskalon.gg.simulation.model.types.ItemType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class ItemPrice {

	private @Getter @Setter ItemType itemType;
	private @Getter @Setter int temporaryEfects;
	private @Getter @Setter int economyPhase;

	public void addTemporaryEfects(int temporaryEfects) {
		this.temporaryEfects += temporaryEfects;
	}

	public int getFinalPrice() {
		return itemType.getBasePrice() + temporaryEfects;
	}

}