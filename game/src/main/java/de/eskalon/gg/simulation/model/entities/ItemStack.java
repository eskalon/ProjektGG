package de.eskalon.gg.simulation.model.entities;

import de.eskalon.gg.simulation.model.types.ItemType;

public final class ItemStack {

	private ItemType type;
	private int count;

	public ItemStack() {
		// default public constructor
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
