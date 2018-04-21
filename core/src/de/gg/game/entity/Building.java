package de.gg.game.entity;

import java.util.ArrayList;
import java.util.List;

import de.gg.game.entity.BuildingTypes.BuildingType;
import de.gg.render.RenderData;
import de.gg.render.SceneRenderer;

/**
 * A building located on an {@link BuildingSlot}.
 */
public class Building {

	private RenderData renderData;
	private BuildingType type;
	private Player owner;
	private int health;
	private List<Cart> cartsOnSite = new ArrayList<>();
	private List<Employee> employees = new ArrayList<>();
	private List<ItemStack> stacks = new ArrayList<>();

	public Building() {
	}

	/**
	 * @return the monetary value this building has.
	 */
	public int getValue() {
		float healthPercentage = health / (float) type.getMaxHealth();
		return Math.round(type.getValue()
				* (healthPercentage > 0.25f ? healthPercentage : 0.25f));
	}

	public void setRenderData(RenderData renderData) {
		this.renderData = renderData;
	}

	/**
	 * @return the model data of the building.
	 * @see SceneRenderer
	 */
	public RenderData getRenderData() {
		return renderData;
	}

	public BuildingType getType() {
		return type;
	}

	public void setType(BuildingType type) {
		this.type = type;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * @return the carts currently located inside the building.
	 */
	public List<Cart> getCartsOnSite() {
		return cartsOnSite;
	}

	public void setCartsOnSite(List<Cart> cartsOnSite) {
		this.cartsOnSite = cartsOnSite;
	}

	/**
	 * @return The employees working in this building. <code>Null</code> if this
	 *         isn't a production building.
	 */
	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	/**
	 * @return The item stacks stored in this building.
	 */
	public List<ItemStack> getStoredStacks() {
		return stacks;
	}

	public void setStoredStacks(List<ItemStack> stacks) {
		this.stacks = stacks;
	}

}
