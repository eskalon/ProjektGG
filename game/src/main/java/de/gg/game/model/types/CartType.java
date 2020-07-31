package de.gg.game.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.gg.game.asset.JSON;
import de.gg.game.asset.JSONLoader.JSONLoaderParameter;

public enum CartType implements ILocalizable {
	BASIC;

	public final static String CART_JSON_DIR = "data/carts";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", CART_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(CartTypeData.class));
	}

	private CartTypeData getData() {
		return TypeRegistry.getInstance().CART_TYPE_DATA.get(this);
	}

	public int getMaxHP() {
		return getData().maxHP;
	}

	public int getPrice() {
		return getData().price;
	}

	public int getAccidentRate() {
		return getData().accidentRate;
	}

	public int getMaxStackSize() {
		return getData().maxStackSize;
	}

	public int getMaxStackCount() {
		return getData().maxStackCount;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.cart." + this.name().toLowerCase() + ".name";
	}

	public class CartTypeData {
		private int maxHP;
		private int price;
		/**
		 * Probability that an accident happens.
		 */
		private int accidentRate;
		private int maxStackSize;
		private int maxStackCount;

		CartTypeData() {
			// default public constructor
		}
	}
}