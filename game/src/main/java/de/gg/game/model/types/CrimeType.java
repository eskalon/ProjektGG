package de.gg.game.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.asset.JSON;
import de.eskalon.commons.asset.JSONLoader.JSONLoaderParameter;
import de.eskalon.commons.lang.ILocalizable;

public enum CrimeType implements ILocalizable {
	TEST;

	public final static String CRIME_JSON_DIR = "data/crimes";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", CRIME_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(CrimeTypeData.class));
	}

	private CrimeTypeData getData() {
		return TypeRegistry.getInstance().CRIME_TYPE_DATA.get(this);
	}

	/**
	 * @return whether this crime is punishable by death.
	 */
	public boolean isCaptialCrime() {
		return getData().captialCrime;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.profession." + this.name().toLowerCase() + ".name";
	}

	public class CrimeTypeData {
		private boolean captialCrime;

		CrimeTypeData() {
			// default public constructor
		}
	}
}