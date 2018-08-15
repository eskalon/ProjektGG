package de.gg.game.entity;

import de.gg.game.type.ItemTypes.ItemType;

public class ItemStack {

	private ItemType type;
	private int count;

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
