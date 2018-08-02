package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

public class CrimeTypes {

	private static List<CrimeType> VALUES;

	private CrimeTypes() {
		// shouldn't get instantiated
	}

	public static void initialize(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		/*
		 * MAYOR = JSONParser.parseFromJson( assetManager.get(MAYOR_JSON_PATH,
		 * Text.class).getString(), CrimeType.class);
		 */
		// VALUES.add(MAYOR);
	}

	public static CrimeType getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public class CrimeType {

		private String name;
		private boolean captialCrime;

		CrimeType() {
		}

		public String getName() {
			return name;
		}

		/**
		 * @return whether this crime is punishable by death.
		 */
		public boolean isCaptialCrime() {
			return captialCrime;
		}

	}

}
