package de.gg.entity;

import java.util.List;

import de.gg.entity.CartTypes.CartType;

public class Cart {

	private CartType type;
	private List<ItemStack> stacks;
	private int hp;
	private Player owner;

	public CartType getType() {
		return type;
	}
	public void setType(CartType type) {
		this.type = type;
	}
	public List<ItemStack> getStacks() {
		return stacks;
	}
	public void setStacks(List<ItemStack> stacks) {
		this.stacks = stacks;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public Player getOwner() {
		return owner;
	}
	public void setOwner(Player owner) {
		this.owner = owner;
	}

}
