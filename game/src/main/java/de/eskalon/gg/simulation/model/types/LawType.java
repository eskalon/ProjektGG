package de.eskalon.gg.simulation.model.types;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.damios.guacamole.IntRange;
import de.eskalon.commons.lang.ILocalizable;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.asset.JSONLoader.JSONLoaderParameter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

public enum LawType implements ILocalizable {
	// FINANCIAL LAWS
	IMPORT_TARIFF, INHERITANCE_TAX;
	// CIRMINAL LAWS
	// [...]

	public final static String LAWS_JSON_DIR = "data/laws";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", LAWS_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(LawTypeData.class));
	}

	private LawTypeData getData() {
		return TypeRegistry.instance().LAW_TYPE_DATA.get(this);
	}

	/**
	 * @return The default value of this law. Can either be a boolean or an
	 *         integer.
	 */
	public Object getDefaultValue() {
		return getData().defaultValue;
	}

	/**
	 * @return If this is an integer law, the range whithin which the value can
	 *         be, otherwise {@code null}.
	 */
	public IntRange getRange() {
		if (!(getData().defaultValue instanceof Integer))
			return null;

		return IntRange.createInclusive(getData().lowerBound,
				getData().upperBound);
	}

	/**
	 * @return A list of every position that can vote on this law. Is empty if
	 *         this law is unchangeable.
	 */
	public List<PositionType> getVoters() {
		return getData().voters;
	}

	/**
	 * @return <code>true</code> when this law can be changed by a single
	 *         position.
	 */
	public boolean isDecree() {
		return getData().voters.size() < 2;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.laws." + this.name().toLowerCase() + ".name";
	}

	@NoArgsConstructor(access = AccessLevel.PACKAGE)
	public class LawTypeData {
		private int upperBound;
		private int lowerBound;
		private Object defaultValue;
		private @Setter List<PositionType> voters;
	}
}