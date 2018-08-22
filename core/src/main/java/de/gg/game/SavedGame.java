package de.gg.game;

import java.util.HashMap;

import de.gg.game.data.GameSessionSetup;
import de.gg.game.world.City;
import de.gg.network.ServerSetup;

/**
 * This class represents a saved game session.
 */
public class SavedGame {

	public GameSessionSetup gameSessionSetup;
	public ServerSetup serverSetup;
	public HashMap<Short, String> clientIdentifiers = new HashMap<>();

	public City city;

	public int currentRound;
	public int lastProcessedTick;

	/**
	 * The states of the processing systems. The key is their
	 * {@linkplain Class#getSimpleName() simple class name}.
	 */
	public HashMap<String, HashMap<String, Object>> processingSystemStates = new HashMap<>();

}
