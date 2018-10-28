
package de.gg.game.types;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.engine.asset.JSON;
import de.gg.engine.asset.JSONLoader.JSONLoaderParameter;
import de.gg.engine.lang.Localizable;
import de.gg.engine.ui.rendering.BaseRenderData;
import de.gg.engine.utils.json.SimpleJSONParser;
import de.gg.game.entities.BuildingSlot;

/**
 * This class holds all the information about the map's setup, e.g. the map
 * boundaries, the building positions, modifiers, etc. This class should get
 * loaded via {@linkplain SimpleJSONParser JSON} and the values of its members
 * shouldn't change!
 */
public enum GameMap implements Localizable {
	BAMBERG;

	public final static String MAP_JSON_DIR = "data/maps";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", MAP_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(GameMapData.class));
	}

	public GameMapData getData() {
		return TypeRegistry.getInstance().MAP_TYPE_DATA.get(this);
	}

	@Override
	public String getUnlocalizedName() {
		return "type.map." + this.name().toLowerCase() + ".name";
	}

	public class GameMapData {
		private List<BuildingSlot> buildingSlots;
		private List<BaseRenderData> cityProps;

		GameMapData() {
			// default public constructor
		}

		/**
		 * @return all buildings slots in the world.
		 */
		public List<BuildingSlot> getBuildingSlots() {
			return buildingSlots;
		}

		/**
		 * @return all static props in the world.
		 */
		public List<BaseRenderData> getCityProps() {
			return cityProps;
		}
	}
}