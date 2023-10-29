package de.eskalon.gg.simulation.model.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a position.
 */
@NoArgsConstructor
public final class Position {

	/**
	 * The id of the character this position is held by.
	 */
	private @Getter @Setter short currentHolder;
	private @Getter List<Short> applicants = new ArrayList<>();

	public Position(short currentHolder) {
		this.currentHolder = currentHolder;
	}

	/**
	 * @return whether this position is currently held.
	 */
	public boolean isHeld() {
		return currentHolder != -1;
	}

	public boolean hasApplicants() {
		return !applicants.isEmpty();
	}

}
