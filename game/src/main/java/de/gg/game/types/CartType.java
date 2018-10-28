package de.gg.game.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.engine.asset.JSON;
import de.gg.engine.asset.JSONLoader.JSONLoaderParameter;
import de.gg.engine.lang.Localizable;

public enum CartType implements Localizable {
	BASIC;

	public final static String CART_JSON_DIR = "data/carts";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", CART_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(CartTypeData.class));
	}

	public CartTypeData getData() {
		return TypeRegistry.getInstance().CART_TYPE_DATA.get(this);
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

		public int getMaxHP() {
			return maxHP;
		}

		public int getPrice() {
			return price;
		}

		public int getAccidentRate() {
			return accidentRate;
		}

		public int getMaxStackSize() {
			return maxStackSize;
		}

		public int getMaxStackCount() {
			return maxStackCount;
		}
	}
}