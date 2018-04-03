package de.gg.event;

public class HouseSelectionEvent {

	/**
	 * The id of the selected object. Can be -1 to denote a click not on an
	 * object.
	 */
	private short id;
	private int clickX, clickY;

	public HouseSelectionEvent(short id, int clickX, int clickY) {
		this.id = id;
		this.clickX = clickX;
		this.clickY = clickY;
	}

	public short getId() {
		return id;
	}

	public int getClickX() {
		return clickX;
	}

	public int getClickY() {
		return clickY;
	}

}
