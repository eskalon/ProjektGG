package de.eskalon.gg.simulation.model.entities;

import java.util.ArrayList;
import java.util.List;

import de.eskalon.gg.asset.ExcludeAnnotationExclusionStrategy.ExcludeFromJSON;
import de.eskalon.gg.graphics.rendering.SelectableRenderData;
import de.eskalon.gg.simulation.model.types.BuildingType;
import lombok.Getter;
import lombok.Setter;

/**
 * A building located on an {@link BuildingSlot}.
 */
public final class Building {

	@ExcludeFromJSON
	private @Getter @Setter SelectableRenderData renderData;
	private @Getter @Setter BuildingType type;
	private @Getter @Setter short owner;
	private @Getter @Setter int health;
	/**
	 * The carts currently located inside the building.
	 */
	private @Getter @Setter List<Cart> cartsOnSite = new ArrayList<>();

	/**
	 * The employees working in this building. {@code Null} if this isn't a
	 * production building.
	 */
	private @Getter @Setter List<Employee> employees = new ArrayList<>();

	/**
	 * @return The item stacks stored in this building.
	 */
	private @Getter @Setter List<ItemStack> stacks = new ArrayList<>();

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

}
