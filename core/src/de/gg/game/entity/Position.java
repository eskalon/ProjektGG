package de.gg.game.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a position.
 */
public class Position {

	/**
	 * The id of the character this position is held by.
	 */
	private short currentHolder;
	private List<Short> applicants = new ArrayList<>();
	/**
	 * Whether there is a vote to remove the current holder after this round.
	 */
	private boolean removeVote = false;

	public Position(short currentHolder) {
		this.currentHolder = currentHolder;
	}

	/**
	 * @return whether this position is currently held.
	 */
	public boolean isHeld() {
		return currentHolder != -1;
	}

	public short getCurrentHolder() {
		return currentHolder;
	}

	public void setCurrentHolder(short currentHolder) {
		this.currentHolder = currentHolder;
	}

	public List<Short> getApplicants() {
		return applicants;
	}

	public boolean isRemoveVote() {
		return removeVote;
	}

	public void setRemoveVote(boolean removeVote) {
		this.removeVote = removeVote;
	}

}
