package de.gg.game.model.entities;

import de.gg.game.model.types.BuildingSlotType;

public final class BuildingSlot {

	private int posX, posZ;
	private int rotationToStreet;
	private BuildingSlotType type;

	private Building building;

	public BuildingSlot() {
		// default public constructor
	}

	/**
	 * @return the center x-position of this building slot.
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return the center z-position of this building slot.
	 */
	public int getPosZ() {
		return posZ;
	}

	/**
	 * @return this rotation has to get applied to the model so that it faces
	 *         the street.
	 */
	public int getRotationToStreet() {
		return rotationToStreet;
	}

	/**
	 * @return the building standing on this slot.
	 */
	public Building getBuilding() {
		return building;
	}

	/**
	 * @return whether a building is built on this slot.
	 */
	public boolean isBuiltOn() {
		return building != null;
	}

	/**
	 * @return the type of this building slot.
	 */
	public BuildingSlotType getType() {
		return type;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

}
