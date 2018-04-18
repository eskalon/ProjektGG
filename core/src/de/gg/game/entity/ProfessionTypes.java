package de.gg.game.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.entity.BuildingTypes.BuildingType;
import de.gg.util.JSONParser;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class ProfessionTypes {

	public static ProfessionType SMITH;
	private static List<ProfessionType> VALUES;

	@Asset(Text.class)
	private static final String SMITH_JSON_PATH = "data/professions/smith.json";

	private ProfessionTypes() {
	}

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		SMITH = JSONParser.parseFromJson(
				assetManager.get(SMITH_JSON_PATH, Text.class).getString(),
				ProfessionType.class);
		VALUES.add(SMITH);
	}

	public static ProfessionType getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public class ProfessionType {

		private String nameLevel1;
		private String nameLevel2;
		private String nameLevel3;
		private String nameLevel4;
		private String nameLevel5;
		private String nameLevel6;
		private int startingGold;
		private int startingBuildingIndex;

		public BuildingType getStartingBuilding() {
			return BuildingTypes.getByIndex(startingBuildingIndex);
		}

		public String getNameLevel1() {
			return nameLevel1;
		}

		public String getNameLevel2() {
			return nameLevel2;
		}

		public String getNameLevel3() {
			return nameLevel3;
		}

		public String getNameLevel4() {
			return nameLevel4;
		}

		public String getNameLevel5() {
			return nameLevel5;
		}

		public String getNameLevel6() {
			return nameLevel6;
		}

		public int getStartingGold() {
			return startingGold;
		}

	}

}
