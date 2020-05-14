
package de.gg.game.model.types;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.asset.JSON;
import de.eskalon.commons.asset.SimpleJSONParser;
import de.eskalon.commons.asset.JSONLoader.JSONLoaderParameter;
import de.eskalon.commons.lang.ILocalizable;
import de.gg.engine.ui.rendering.BaseRenderData;
import de.gg.game.model.entities.BuildingSlot;

/**
 * This class holds all the information about the map's setup, e.g. the map
 * boundaries, the building positions, modifiers, etc. This class should get
 * loaded via {@linkplain SimpleJSONParser JSON} and the values of its members
 * shouldn't change!
 */
public enum GameMap implements ILocalizable {
	BAMBERG;

	public final static String MAP_JSON_DIR = "data/maps";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", MAP_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(GameMapData.class));
	}

	private GameMapData getData() {
		return TypeRegistry.getInstance().MAP_TYPE_DATA.get(this);
	}

	/**
	 * @return all buildings slots in the world.
	 */
	public List<BuildingSlot> getBuildingSlots() {
		return getData().buildingSlots;
	}

	/**
	 * @return all static props in the world.
	 */
	public List<BaseRenderData> getCityProps() {
		return getData().cityProps;
	}

	public String getSkyboxPath() {
		return getData().skyboxPath;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.map." + this.name().toLowerCase() + ".name";
	}

	public class GameMapData {
		private String skyboxPath;
		private List<BuildingSlot> buildingSlots;
		private List<BaseRenderData> cityProps;

		GameMapData() {
			// default public constructor
		}
	}
}