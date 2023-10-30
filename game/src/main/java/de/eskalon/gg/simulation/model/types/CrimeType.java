package de.eskalon.gg.simulation.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.asset.JSONLoader.JSONLoaderParameter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
		return TypeRegistry.instance().CRIME_TYPE_DATA.get(this);
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

	@NoArgsConstructor(access = AccessLevel.PACKAGE)
	public class CrimeTypeData {
		private boolean captialCrime;
	}
}