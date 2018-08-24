package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.type.BuildingTypes.BuildingType;
import de.gg.util.asset.JSON;
import de.gg.util.asset.JSONLoader.JSONLoaderParameter;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class ProfessionTypes {

	public static ProfessionType SMITH;
	public static ProfessionType TEACHER;
	private static List<ProfessionType> VALUES;

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> SMITH_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/professions/smith.json",
				JSON.class, new JSONLoaderParameter(ProfessionType.class));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> TEACHER_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/professions/teacher.json",
				JSON.class, new JSONLoaderParameter(ProfessionType.class));
	}

	private ProfessionTypes() {
		// shouldn't get instantiated
	}

	public static List<ProfessionType> getValues() {
		return VALUES;
	}

	public static void initialize(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		SMITH = assetManager.get(SMITH_JSON_PATH())
				.getData(ProfessionType.class);
		VALUES.add(SMITH);

		TEACHER = assetManager.get(TEACHER_JSON_PATH())
				.getData(ProfessionType.class);
		VALUES.add(TEACHER);
	}

	public static ProfessionType getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public static class ProfessionType {

		private String nameLevel1;
		private String nameLevel2;
		private String nameLevel3;
		private String nameLevel4;
		private String nameLevel5;
		private String nameLevel6;
		private int startingGold;
		private int startingBuildingIndex;
		private String iconFileName;

		ProfessionType() {
		}

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

		public String getIconFileName() {
			return iconFileName;
		}

		@Override
		public int hashCode() {
			return Objects.hash(nameLevel1);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			return Objects.equals(nameLevel1,
					((ProfessionType) obj).nameLevel1);
		}

	}

}
