package de.gg.game.model.types;

import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.google.common.collect.Range;

import de.eskalon.commons.asset.JSON;
import de.eskalon.commons.asset.JSONLoader.JSONLoaderParameter;
import de.eskalon.commons.lang.ILocalizable;

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
		return TypeRegistry.getInstance().LAW_TYPE_DATA.get(this);
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
	public Range<Integer> getRange() {
		if (!(getData().defaultValue instanceof Integer))
			return null;

		return Range.closed(getData().lowerBound, getData().upperBound);
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

	public class LawTypeData {
		private int upperBound;
		private int lowerBound;
		private Object defaultValue;
		private List<PositionType> voters;

		LawTypeData() {
			// default public constructor
		}

		protected void setVoters(List<PositionType> voters) {
			this.voters = voters;
		}
	}
}