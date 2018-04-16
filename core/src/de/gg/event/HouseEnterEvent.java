package de.gg.event;

/**
 * Is posted when the local player enters a house.
 */
public class HouseEnterEvent {

	private short id;

	public HouseEnterEvent(short id) {
		this.id = id;
	}

	/**
	 * @return the id of the entered house.
	 */
	public short getId() {
		return id;
	}

}
