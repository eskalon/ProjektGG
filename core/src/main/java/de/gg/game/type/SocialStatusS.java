package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.type.PositionTypes.PositionType;
import de.gg.util.asset.JSON;
import de.gg.util.asset.JSONLoader.JSONLoaderParameter;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class SocialStatusS {

	public static SocialStatus NON_CITIZEN, CITIZEN, PATRICIAN, CAVALIER, BARON;
	private static List<SocialStatus> VALUES;

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> NON_CITIZEN_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/social_status/non_citizen.json",
				JSON.class, new JSONLoaderParameter(SocialStatus.class));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> CITIZEN_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/social_status/citizen.json",
				JSON.class, new JSONLoaderParameter(SocialStatus.class));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> PATRICIAN_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/social_status/patrician.json",
				JSON.class, new JSONLoaderParameter(SocialStatus.class));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> CAVALIER_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/social_status/cavalier.json",
				JSON.class, new JSONLoaderParameter(SocialStatus.class));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> BARON_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/social_status/baron.json",
				JSON.class, new JSONLoaderParameter(SocialStatus.class));
	}

	private SocialStatusS() {
		// shouldn't get instantiated
	}

	public static List<SocialStatus> getValues() {
		return VALUES;
	}

	public static void initialize(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		NON_CITIZEN = assetManager.get(NON_CITIZEN_JSON_PATH())
				.getData(SocialStatus.class);
		VALUES.add(NON_CITIZEN);

		CITIZEN = assetManager.get(CITIZEN_JSON_PATH())
				.getData(SocialStatus.class);
		VALUES.add(CITIZEN);

		PATRICIAN = assetManager.get(PATRICIAN_JSON_PATH())
				.getData(SocialStatus.class);
		VALUES.add(PATRICIAN);

		CAVALIER = assetManager.get(CAVALIER_JSON_PATH())
				.getData(SocialStatus.class);
		VALUES.add(CAVALIER);

		BARON = assetManager.get(BARON_JSON_PATH()).getData(SocialStatus.class);
		VALUES.add(BARON);
	}

	public static SocialStatus getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public class SocialStatus {

		private int level;
		private String name;
		private int fortuneRequirement;
		private int positionLevelRequirement = -1;
		private boolean isTitle = false;

		public String getName() {
			return name;
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

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			return Objects.equals(name, ((SocialStatus) obj).name);
		}

	}

}
