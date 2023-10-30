package de.eskalon.gg.simulation.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.asset.JSONLoader.JSONLoaderParameter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
		return TypeRegistry.instance().CART_TYPE_DATA.get(this);
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

	@NoArgsConstructor(access = AccessLevel.PACKAGE)
	public class CartTypeData {
		private int maxHP;
		private int price;
		/**
		 * Probability that an accident happens.
		 */
		private int accidentRate;
		private int maxStackSize;
		private int maxStackCount;
	}
}