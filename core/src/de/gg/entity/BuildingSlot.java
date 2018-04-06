package de.gg.entity;

public class BuildingSlot {

	private int posX, posY;
	private int rotationToStreet;
	private BuildingSlotType type;

	private Building building;

	public BuildingSlot() {
	}

	/**
	 * @return The center x-position of this building slot.
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return The center y-position of this building slot.
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * @return This rotation has to get applied to the model so that it faces
	 *         the street.
	 */
	public int getRotationToStreet() {
		return rotationToStreet;
	}

	/**
	 * @return THe building standing on this slot.
	 */
	public Building getBuilding() {
		return building;
	}

	public boolean isBuiltOn() {
		return building != null;
	}

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
