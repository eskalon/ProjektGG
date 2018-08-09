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
	public HashMap<Short, String> clientIdentifiers;

	public City city;

	public int currentRound;

}
