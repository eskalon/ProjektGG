package de.gg.game.model.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a position.
 */
public final class Position {

	private short currentHolder;
	private List<Short> applicants = new ArrayList<>();

	public Position() {
		// default public constructor
	}

	public Position(short currentHolder) {
		this.currentHolder = currentHolder;
	}

	/**
	 * @return whether this position is currently held.
	 */
	public boolean isHeld() {
		return currentHolder != -1;
	}

	/**
	 * @return the id of the character this position is held by.
	 */
	public short getCurrentHolder() {
		return currentHolder;
	}

	public void setCurrentHolder(short currentHolder) {
		this.currentHolder = currentHolder;
	}

	public List<Short> getApplicants() {
		return applicants;
	}

	public boolean hasApplicants() {
		return !applicants.isEmpty();
	}

}
