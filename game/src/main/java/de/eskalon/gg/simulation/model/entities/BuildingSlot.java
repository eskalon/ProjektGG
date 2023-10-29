package de.eskalon.gg.simulation.model.entities;

import de.eskalon.gg.simulation.model.types.BuildingSlotType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class BuildingSlot {

	/**
	 * The center x-position of this building slot.
	 */
	private @Getter int posX;
	/**
	 * The center z-position of this building slot.
	 */
	private @Getter int posZ;
	/**
	 * This rotation has to get applied to the model so that it faces the
	 * street.
	 */
	private @Getter int rotationToStreet;
	/**
	 * The type of this building slot.
	 */
	private @Getter BuildingSlotType type;
	/**
	 * The building standing on this slot.
	 */
	private @Getter @Setter Building building;

	/**
	 * @return whether a building is built on this slot.
	 */
	public boolean isBuiltOn() {
		return building != null;
	}

}
