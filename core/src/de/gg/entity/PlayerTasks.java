package de.gg.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.util.JSONParser;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class PlayerTasks {

	public static PlayerTask LEARNING_SKILL, UPGRADING_MASTER;
	private static List<PlayerTask> VALUES;

	@Asset(Text.class)
	private static final String LEARNING_AGILITY_JSON_PATH = "data/misc/learning_skill_task.json";
	@Asset(Text.class)
	private static final String UPGRADING_MASTER_JSON_PATH = "data/misc/updgrading_master_task.json";

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		LEARNING_SKILL = JSONParser.parseFromJson(assetManager
				.get(LEARNING_AGILITY_JSON_PATH, Text.class).getString(),
				PlayerTask.class);
		VALUES.add(LEARNING_SKILL);

		UPGRADING_MASTER = JSONParser.parseFromJson(assetManager
				.get(UPGRADING_MASTER_JSON_PATH, Text.class).getString(),
				PlayerTask.class);
		VALUES.add(UPGRADING_MASTER);
	}

	public static PlayerTask getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public class PlayerTask {

		/**
		 * The duration it takes to perform this task in its basic form.
		 */
		private int basicDuration;

		public int getBasicDuration() {
			return basicDuration;
		}

	}

}
