package de.gg.game.model.entities;

import de.gg.game.model.types.ItemType;

public class ItemStack {

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
