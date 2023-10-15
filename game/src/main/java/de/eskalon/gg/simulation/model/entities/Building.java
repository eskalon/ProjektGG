package de.eskalon.gg.simulation.model.entities;

import java.util.ArrayList;
import java.util.List;

import de.eskalon.gg.asset.ExcludeAnnotationExclusionStrategy.ExcludeFromJSON;
import de.eskalon.gg.graphics.rendering.GameRenderer;
import de.eskalon.gg.graphics.rendering.SelectableRenderData;
import de.eskalon.gg.simulation.model.types.BuildingType;

/**
 * A building located on an {@link BuildingSlot}.
 */
public final class Building {

	@ExcludeFromJSON
	private SelectableRenderData renderData;
	private BuildingType type;
	private short owner;
	private int health;
	private List<Cart> cartsOnSite = new ArrayList<>();
	private List<Employee> employees = new ArrayList<>();
	private List<ItemStack> stacks = new ArrayList<>();

	public Building() {
		// default public constructor
	}

	/**
	 * @return the monetary value this building has.
	 */
	public int getValue() {
		float healthPercentage = health / (float) type.getMaxHealth();
		return Math.round(type.getValue()
				* (healthPercentage > 0.25F ? healthPercentage : 0.25F));
	}

	public void setRenderData(SelectableRenderData renderData) {
		this.renderData = renderData;
	}

	/**
	 * @return the model data of the building.
	 * @see GameRenderer
	 */
	public SelectableRenderData getRenderData() {
		return renderData;
	}

	public BuildingType getType() {
		return type;
	}

	public void setType(BuildingType type) {
		this.type = type;
	}

	public short getOwner() {
		return owner;
	}

	public void setOwner(short owner) {
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
