package de.gg.screens.exception;

import de.gg.core.ScreenGame;

/**
 * Thrown when a pushed screen is not found.
 *
 * @see ScreenGame#pushScreen(String)
 */
public class ScreenNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ScreenNotFoundException(String message) {
		super(message);
	}

}
