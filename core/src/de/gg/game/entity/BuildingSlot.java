package de.gg.game.entity;

public class BuildingSlot {

	private int posX, posY;
	private int rotationToStreet;
	private BuildingSlotType type;

	private Building building;

	public BuildingSlot() {
	}

	/**
	 * @return the center x-position of this building slot.
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return the center y-position of this building slot.
	 */
	public int getPosY() {
		return posY;
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

	public enum BuildingSlotType {
		CITY_NORMAL, FOREST;
	}

}
