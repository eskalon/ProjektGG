package de.gg.game.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.gg.game.asset.JSON;
import de.gg.game.asset.JSONLoader.JSONLoaderParameter;

public enum ProfessionType implements ILocalizable {
	SMITH, TEACHER;

	public final static String PROFESSION_JSON_DIR = "data/professions";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", PROFESSION_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(ProfessionTypeData.class));
	}

	private ProfessionTypeData getData() {
		return TypeRegistry.getInstance().PROFESSION_TYPE_DATA.get(this);
	}

	public BuildingType getStartingBuilding() {
		return BuildingType.values()[getData().startingBuildingIndex];
	}

	public int getStartingGold() {
		return getData().startingGold;
	}

	public String getIconFileName() {
		return getData().iconFileName;
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
	}
}
