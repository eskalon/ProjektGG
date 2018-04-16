package de.gg.entity;

import de.gg.entity.ItemTypes.ItemType;

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
