package de.gg.game.entity;

import java.util.ArrayList;
import java.util.List;

import de.gg.game.type.CartTypes.CartType;

public class Cart {

	private CartType type;
	private List<ItemStack> stacks = new ArrayList<>();
	private int hp;
	private Player owner;

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

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

}
