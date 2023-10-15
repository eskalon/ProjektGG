package de.eskalon.gg.simulation;

import java.util.HashMap;

import de.eskalon.gg.simulation.model.World;

public class GameState {

	public World world;

	public int currentRound;
	public int lastProcessedTick;

	/**
	 * The states of the processing systems. The key is their
	 * {@linkplain Class#getSimpleName() simple class name}.
	 */
	public HashMap<String, HashMap<String, Object>> processingSystemStates = new HashMap<>();

}
