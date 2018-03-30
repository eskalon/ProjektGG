package dev.gg.data;

import java.util.ArrayList;

/**
 * This class holds all the information about the map's setup, e.g. the map
 * boundaries, the building positions, modifiers, etc. This class should get
 * loaded via {@linkplain JSONParser JSON} and the values of its members
 * shouldn't change!
 */
public class GameMap {

	private transient static ArrayList<GameMap> MAPS = new ArrayList<>();

	/**
	 * @return A list of all supported maps.
	 */
	public static ArrayList<GameMap> getMaps() {
		if (MAPS.isEmpty()) {
			// LOAD THE MAPS
			// TODO should get done via json
			GameMap bamberg = new GameMap();

			MAPS.add(bamberg);

		}

		return MAPS;
	}

	public GameMap() {
	}

}
