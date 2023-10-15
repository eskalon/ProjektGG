package de.eskalon.gg.simulation.model.entities;

import de.eskalon.gg.simulation.model.types.ItemType;

public final class ItemPrice {

	private ItemType itemType;
	private int temporaryEfects;
	private int economyPhase;

	public ItemPrice() {
		// default public constructor
	}

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