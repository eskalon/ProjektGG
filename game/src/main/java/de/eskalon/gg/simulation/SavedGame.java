package de.eskalon.gg.simulation;

import java.util.HashMap;

/**
 * This class represents a saved game session.
 */
public class SavedGame {

	public String saveName;
	public GameSetup gameSessionSetup;
	public HashMap<Short, String> clientIdentifiers = new HashMap<>();

	public GameState state;

}
