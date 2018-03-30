package de.gg.exception;

import de.gg.core.ProjektGG;

/**
 * Thrown when a pushed screen is not found.
 * 
 * @see ProjektGG#pushScreen(String)
 */
public class ScreenNotFoundException extends RuntimeException {

	public ScreenNotFoundException(String message) {
		super(message);
	}

}
