package de.gg.game.events;

/**
 * Is posted when the local player selects a house.
 */
public class HouseSelectionEvent {

	/**
	 * The id of the selected object. Can be -1 to denote a click not on an
	 * object.
	 */
	private short id;

	public HouseSelectionEvent(short id) {
		this.id = id;
	}

	/**
	 * @return the id of the selected object. Can be -1 to denote a click not on
	 *         an object.
	 */
	public short getId() {
		return id;
	}

}
