package de.gg.game.model.entities;

import java.util.ArrayList;
import java.util.List;

import de.gg.game.model.types.CartType;

public final class Cart {

	private CartType type;
	private List<ItemStack> stacks = new ArrayList<>();
	private int hp;
	private short owner;

	public Cart() {
		// default public constructor
	}

	public CartType getType() {
		return type;
	}

	public void setType(CartType type) {
		this.type = type;
	}

	public List<ItemStack> getStoredStacks() {
		return stacks;
	}

	public void setStoredStacks(List<ItemStack> stacks) {
		this.stacks = stacks;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public short getOwner() {
		return owner;
	}

	public void setOwner(short owner) {
		this.owner = owner;
	}

}
