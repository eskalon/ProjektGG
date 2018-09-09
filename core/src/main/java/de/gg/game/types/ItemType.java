package de.gg.game.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.game.entities.ItemPrice;
import de.gg.lang.Localizable;
import de.gg.utils.asset.JSON;
import de.gg.utils.asset.JSONLoader.JSONLoaderParameter;

public enum ItemType implements Localizable {
	TEST;

	public final static String ITEM_JSON_DIR = "data/items";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", ITEM_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(ItemTypeData.class));
	}

	public ItemTypeData getData() {
		return TypeRegistry.getInstance().ITEM_TYPE_DATA.get(this);
	}

	@Override
	public String getUnlocalizedName() {
		return "type.item." + this.name().toLowerCase() + ".name";
	}

	public class ItemTypeData {
		private String iconFileName;
		private int basePrice;
		private boolean equipableByPlayer = false;

		ItemTypeData() {
			// default public constructor
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