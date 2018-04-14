package de.gg.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

public class CrimeTypes {

	private static List<CrimeType> VALUES;

	public static void finishLoading(AssetManager assetManager) {
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
		private boolean punishableByDeath;

		public String getName() {
			return name;
		}
		public boolean isPunishableByDeath() {
			return punishableByDeath;
		}

	}

}
