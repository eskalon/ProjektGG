package de.gg.entity;

import de.gg.entity.ItemTypes.ItemType;

public class ItemPrice {

	private ItemType itemType;
	private int temporaryEfects;
	private int economyPhase;

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public int getTemporaryEfects() {
		return temporaryEfects;
	}

	public void addTemporaryEfects(int temporaryEfects) {
		this.temporaryEfects += temporaryEfects;
	}

	public void setTemporaryEfects(int temporaryEfects) {
		this.temporaryEfects = temporaryEfects;
	}

	public int getEconomyPhase() {
		return economyPhase;
	}

	public void setEconomyPhase(int economyPhase) {
		this.economyPhase = economyPhase;
	}

	public int getFinalPrice() {
		return itemType.getBasePrice() + temporaryEfects;
	}

}