package de.eskalon.gg.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Is posted when the local player enters a house.
 */
@AllArgsConstructor
public class HouseEnterEvent {

	/**
	 * The id of the entered house.
	 */
	private @Getter short id;

}
