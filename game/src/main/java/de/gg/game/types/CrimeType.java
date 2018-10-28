package de.gg.game.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.engine.asset.JSON;
import de.gg.engine.asset.JSONLoader.JSONLoaderParameter;
import de.gg.engine.lang.Localizable;

public enum CrimeType implements Localizable {
	TEST;

	public final static String CRIME_JSON_DIR = "data/crimes";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", CRIME_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(CrimeTypeData.class));
	}

	public CrimeTypeData getData() {
		return TypeRegistry.getInstance().CRIME_TYPE_DATA.get(this);
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

		/**
		 * @return whether this crime is punishable by death.
		 */
		public boolean isCaptialCrime() {
			return captialCrime;
		}
	}
}