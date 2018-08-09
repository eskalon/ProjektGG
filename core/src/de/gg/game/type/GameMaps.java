
package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.entity.BuildingSlot;
import de.gg.render.RenderData;
import de.gg.util.asset.Text;
import de.gg.util.json.SimpleJSONParser;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This class holds all the information about the map's setup, e.g. the map
 * boundaries, the building positions, modifiers, etc. This class should get
 * loaded via {@linkplain SimpleJSONParser JSON} and the values of its members
 * shouldn't change!
 */
public class GameMaps {

	public static GameMap BAMBERG;
	private static List<GameMap> VALUES;

	@Asset(Text.class)
	private static final String BAMBERG_JSON_PATH = "data/maps/bamberg.json";

	private GameMaps() {
		// shouldn't get instantiated
	}

	/**
	 * @return a list of all supported maps.
	 */
	public static List<GameMap> getMaps() {
		return VALUES;
	}

	public static void initialize(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		BAMBERG = SimpleJSONParser.parseFromJson(
				assetManager.get(BAMBERG_JSON_PATH, Text.class).getString(),
				GameMap.class);
		VALUES.add(BAMBERG);
	}

	public static GameMap getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	/**
	 * An in-game map.
	 */
	public class GameMap {

		private String name;
		private List<BuildingSlot> buildingSlots;
		private List<RenderData> cityProps;

		GameMap() {
		}

		public String getName() {
			return name;
		}

		/**
		 * @return all buildings slots in the city.
		 */
		public List<BuildingSlot> getBuildingSlots() {
			return buildingSlots;
		}

		/**
		 * @return all static props in the city.
		 */
		public List<RenderData> getCityProps() {
			return cityProps;
		}

	}

}
