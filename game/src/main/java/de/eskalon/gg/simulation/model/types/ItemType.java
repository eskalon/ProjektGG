package de.eskalon.gg.simulation.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.asset.JSONLoader.JSONLoaderParameter;
import de.eskalon.gg.simulation.model.entities.ItemPrice;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public enum ItemType implements ILocalizable {
	TEST;

	public final static String ITEM_JSON_DIR = "data/items";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", ITEM_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(ItemTypeData.class));
	}

	private ItemTypeData getData() {
		return TypeRegistry.instance().ITEM_TYPE_DATA.get(this);
	}

	public String getIconPath() {
		return getData().iconPath;
	}

	/**
	 * @return the base price for this item.
	 * @see ItemPrice
	 */
	public int getBasePrice() {
		return getData().basePrice;
	}

	/**
	 * @return whether this item can get used/equipped by the player.
	 */
	public boolean isEquipableByPlayer() {
		return getData().equipableByPlayer;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.item." + this.name().toLowerCase() + ".name";
	}

	@NoArgsConstructor(access = AccessLevel.PACKAGE)
	public class ItemTypeData {
		private String iconPath;
		private int basePrice;
		private boolean equipableByPlayer = false;
	}
}