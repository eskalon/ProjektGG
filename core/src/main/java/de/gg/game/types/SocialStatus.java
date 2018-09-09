package de.gg.game.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.lang.Localizable;
import de.gg.utils.asset.JSON;
import de.gg.utils.asset.JSONLoader.JSONLoaderParameter;

public enum SocialStatus implements Localizable {
	NON_CITIZEN, CITIZEN, PATRICIAN, CAVALIER, BARON;

	public final static String SOCIAL_STATUS_JSON_DIR = "data/social_status";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", SOCIAL_STATUS_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(SocialStatusData.class));
	}

	public SocialStatusData getData() {
		return TypeRegistry.getInstance().SOCIAL_STATUS_TYPE_DATA.get(this);
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

		public int getFortuneRequirement() {
			return fortuneRequirement;
		}

		/**
		 * @return the {@linkplain PositionType#getLevel() level of a position}
		 *         if this is required for this social status. Else
		 *         <code>-1</code>.
		 */
		public int getPositionLevelRequirement() {
			return positionLevelRequirement;
		}

		/**
		 * @return Whether the characters name has to include the appropriate
		 *         nobility title.
		 */
		public boolean isTitle() {
			return isTitle;
		}

		public int getLevel() {
			return level;
		}
	}

}