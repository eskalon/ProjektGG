package de.gg.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.entity.SocialStatusS.SocialStatus;
import de.gg.util.JSONParser;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class PositionTypes {

	public static PositionType MAYOR;
	private static List<PositionType> VALUES;

	@Asset(Text.class)
	private static final String MAYOR_JSON_PATH = "data/positions/mayor.json";

	private PositionTypes() {
	}

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		MAYOR = JSONParser.parseFromJson(
				assetManager.get(MAYOR_JSON_PATH, Text.class).getString(),
				PositionType.class);
		VALUES.add(MAYOR);
	}

	public static PositionType getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public class PositionType {

		private int level;
		private int statusRequirementIndex;

		PositionType() {
		}

		public int getLevel() {
			return level;
		}

		/**
		 * @return The required status for this position. Null if not status is
		 *         needed.
		 */
		public SocialStatus getStatusRequirement() {
			return SocialStatusS.getByIndex(statusRequirementIndex);
		}

	}

}
