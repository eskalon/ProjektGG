package de.eskalon.gg.simulation.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.asset.JSONLoader.JSONLoaderParameter;

public enum SocialStatus implements ILocalizable {
	NON_CITIZEN, CITIZEN, PATRICIAN, CAVALIER, BARON;

	public final static String SOCIAL_STATUS_JSON_DIR = "data/social_status";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", SOCIAL_STATUS_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(SocialStatusData.class));
	}

	private SocialStatusData getData() {
		return TypeRegistry.instance().SOCIAL_STATUS_TYPE_DATA.get(this);
	}

	public int getFortuneRequirement() {
		return getData().fortuneRequirement;
	}

	/**
	 * @return the {@linkplain PositionType#getLevel() level of a position} if
	 *         this is required for this social status. Else <code>-1</code>.
	 */
	public int getPositionLevelRequirement() {
		return getData().positionLevelRequirement;
	}

	/**
	 * @return Whether the characters name has to include the appropriate
	 *         nobility title.
	 */
	public boolean isTitle() {
		return getData().isTitle;
	}

	public int getLevel() {
		return getData().level;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.profession." + this.name().toLowerCase() + ".name";
	}

	public class SocialStatusData {
		private int level;
		private int fortuneRequirement;
		private int positionLevelRequirement = -1;
		private boolean isTitle = false;

		SocialStatusData() {
			// default public constructor
		}
	}

}