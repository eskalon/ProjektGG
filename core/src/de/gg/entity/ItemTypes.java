package de.gg.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

public class ItemTypes {

	private static List<ItemType> VALUES;

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		/*
		 * MAYOR = JSONParser.parseFromJson( assetManager.get(MAYOR_JSON_PATH,
		 * Text.class).getString(), ItemType.class);
		 */
		// VALUES.add(MAYOR);
	}

	public static ItemType getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public class ItemType {

		private String name;
		private String iconFileName;
		private int basePrice;
		private boolean equipableByPlayer = false;

		public String getName() {
			return name;
		}
		public String getIconFileName() {
			return iconFileName;
		}
		public int getBasePrice() {
			return basePrice;
		}
		public boolean isEquipableByPlayer() {
			return equipableByPlayer;
		}

	}

}
