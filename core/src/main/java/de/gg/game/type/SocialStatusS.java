package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.type.PositionTypes.PositionType;
import de.gg.util.asset.Text;
import de.gg.util.json.SimpleJSONParser;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class SocialStatusS {

	public static SocialStatus NON_CITIZEN, CITIZEN, PATRICIAN, CAVALIER, BARON;
	private static List<SocialStatus> VALUES;

	@Asset(Text.class)
	private static final String NON_CITIZEN_JSON_PATH = "data/social_status/non_citizen.json";
	@Asset(Text.class)
	private static final String CITIZEN_JSON_PATH = "data/social_status/citizen.json";
	@Asset(Text.class)
	private static final String PATRICIAN_JSON_PATH = "data/social_status/patrician.json";
	@Asset(Text.class)
	private static final String CAVALIER_JSON_PATH = "data/social_status/cavalier.json";
	@Asset(Text.class)
	private static final String BARON_JSON_PATH = "data/social_status/baron.json";

	private SocialStatusS() {
		// shouldn't get instantiated
	}

	public static List<SocialStatus> getValues() {
		return VALUES;
	}

	public static void initialize(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		NON_CITIZEN = SimpleJSONParser.parseFromJson(
				assetManager.get(NON_CITIZEN_JSON_PATH, Text.class).getString(),
				SocialStatus.class);
		VALUES.add(NON_CITIZEN);
		CITIZEN = SimpleJSONParser.parseFromJson(
				assetManager.get(CITIZEN_JSON_PATH, Text.class).getString(),
				SocialStatus.class);
		VALUES.add(CITIZEN);
		PATRICIAN = SimpleJSONParser.parseFromJson(
				assetManager.get(PATRICIAN_JSON_PATH, Text.class).getString(),
				SocialStatus.class);
		VALUES.add(PATRICIAN);
		CAVALIER = SimpleJSONParser.parseFromJson(
				assetManager.get(CAVALIER_JSON_PATH, Text.class).getString(),
				SocialStatus.class);
		VALUES.add(CAVALIER);
		BARON = SimpleJSONParser.parseFromJson(
				assetManager.get(BARON_JSON_PATH, Text.class).getString(),
				SocialStatus.class);
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
