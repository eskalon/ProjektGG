package de.eskalon.gg.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Is posted when the local player selects a house.
 */
@AllArgsConstructor
public class HouseSelectionEvent {

	/**
	 * The id of the selected object. Can be -1 to denote a click not on an
	 * object.
	 */
	private @Getter short id;

}
