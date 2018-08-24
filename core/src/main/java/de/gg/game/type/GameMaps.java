
package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.entity.BuildingSlot;
import de.gg.render.RenderData;
import de.gg.util.asset.JSON;
import de.gg.util.asset.JSONLoader.JSONLoaderParameter;
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

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> BAMBERG_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/maps/bamberg.json", JSON.class,
				new JSONLoaderParameter(GameMap.class));
	}

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

		BAMBERG = assetManager.get(BAMBERG_JSON_PATH()).getData(GameMap.class);
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

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			return Objects.equals(name, ((GameMap) obj).name);
		}

	}

}
