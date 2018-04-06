package de.gg.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.util.JSONParser;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class SocialStatusS {

	private static SocialStatus NON_CITIZEN, CITIZEN, PATRICIAN, CAVALIER,
			BARON;
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
	}

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		NON_CITIZEN = JSONParser.parseFromJson(
				assetManager.get(NON_CITIZEN_JSON_PATH, Text.class).getString(),
				SocialStatus.class);
		VALUES.add(NON_CITIZEN);
		CITIZEN = JSONParser.parseFromJson(
				assetManager.get(CITIZEN_JSON_PATH, Text.class).getString(),
				SocialStatus.class);
		VALUES.add(CITIZEN);
		PATRICIAN = JSONParser.parseFromJson(
				assetManager.get(PATRICIAN_JSON_PATH, Text.class).getString(),
				SocialStatus.class);
		VALUES.add(PATRICIAN);
		CAVALIER = JSONParser.parseFromJson(
				assetManager.get(CAVALIER_JSON_PATH, Text.class).getString(),
				SocialStatus.class);
		VALUES.add(CAVALIER);
		BARON = JSONParser.parseFromJson(
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

		public int getPositionLevelRequirement() {
			return positionLevelRequirement;
		}

		public boolean isTitle() {
			return isTitle;
		}

	}

}
