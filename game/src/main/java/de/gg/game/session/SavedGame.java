package de.gg.game.session;

import java.util.HashMap;

import de.gg.engine.network.ServerSettings;
import de.gg.game.model.World;

/**
 * This class represents a saved game session.
 */
public class SavedGame {

	public GameSessionSetup gameSessionSetup;
	public ServerSettings serverSetup;
	public HashMap<Short, String> clientIdentifiers = new HashMap<>();

	public World world;

	public int currentRound;
	public int lastProcessedTick;

	/**
	 * The states of the processing systems. The key is their
	 * {@linkplain Class#getSimpleName() simple class name}.
	 */
	public HashMap<String, HashMap<String, Object>> processingSystemStates = new HashMap<>();

}
