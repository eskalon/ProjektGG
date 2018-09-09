package de.gg.screens.exception;

import de.gg.core.ProjektGG;

/**
 * Thrown when a pushed screen is not found.
 *
 * @see ProjektGG#pushScreen(String)
 */
public class ScreenNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ScreenNotFoundException(String message) {
		super(message);
	}

}
