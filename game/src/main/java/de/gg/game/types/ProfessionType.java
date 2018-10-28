package de.gg.game.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.engine.asset.JSON;
import de.gg.engine.asset.JSONLoader.JSONLoaderParameter;
import de.gg.engine.lang.Localizable;

public enum ProfessionType implements Localizable {
	SMITH, TEACHER;

	public final static String PROFESSION_JSON_DIR = "data/professions";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", PROFESSION_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(ProfessionTypeData.class));
	}

	public ProfessionTypeData getData() {
		return TypeRegistry.getInstance().PROFESSION_TYPE_DATA.get(this);
	}

	@Override
	public String getUnlocalizedName() {
		return "type.profession." + this.name().toLowerCase() + ".name";
	}

	public class ProfessionTypeData {
		private int startingGold;
		private int startingBuildingIndex;
		private String iconFileName;

		ProfessionTypeData() {
			// default public constructor
		}

		public BuildingType getStartingBuilding() {
			return BuildingType.values()[startingBuildingIndex];
		}

		public int getStartingGold() {
			return startingGold;
		}

		public String getIconFileName() {
			return iconFileName;
		}
	}
}
