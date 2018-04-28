package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.entity.ItemPrice;

public class ItemTypes {

	private static List<ItemType> VALUES;
	
	private ItemTypes() {
		// shouldn't get instantiated
	}

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

		ItemType() {
		}

		public String getName() {
			return name;
		}

		public String getIconFileName() {
			return iconFileName;
		}

		/**
		 * @return the base price for this item.
		 * @see ItemPrice
		 */
		public int getBasePrice() {
			return basePrice;
		}

		/**
		 * @return whether this item can get used/equipped by the player.
		 */
		public boolean isEquipableByPlayer() {
			return equipableByPlayer;
		}

	}

}