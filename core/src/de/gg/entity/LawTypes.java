package de.gg.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.google.common.collect.Range;

import de.gg.util.JSONParser;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class LawTypes {

	// FINANCIAL LAWS
	public static LawType IMPORT_TARIFF;
	// CIRMINAL LAWS
	// [...]

	private static List<LawType> VALUES;

	@Asset(Text.class)
	private static final String IMPORT_TARIFF_JSON_PATH = "data/laws/import_tariff.json";

	private LawTypes() {
	}

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		IMPORT_TARIFF = JSONParser.parseFromJson(assetManager
				.get(IMPORT_TARIFF_JSON_PATH, Text.class).getString(),
				LawType.class);
		VALUES.add(IMPORT_TARIFF);
	}

	public static LawType getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public class LawType {

		private String name;
		private int upperBound;
		private int lowerBound;
		private Object defaultValue;

		public String getName() {
			return name;
		}

		/**
		 * @return THe default value of this law. Can either be a boolean or an
		 *         integer.
		 */
		public Object getDefaultValue() {
			return defaultValue;
		}

		/**
		 * @return If this is an integer law, the range whithin which the value
		 *         can be.
		 */
		public Range<Integer> getRange() {
			return Range.closed(lowerBound, upperBound);
		}
	}

}
