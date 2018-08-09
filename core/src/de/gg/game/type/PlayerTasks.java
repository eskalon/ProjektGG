package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.entity.Player;
import de.gg.util.asset.Text;
import de.gg.util.json.SimpleJSONParser;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class PlayerTasks {

	public static PlayerTask LEARNING_SKILL, UPGRADING_MASTER;
	private static List<PlayerTask> VALUES;

	@Asset(Text.class)
	private static final String LEARNING_AGILITY_JSON_PATH = "data/misc/learning_skill_task.json";
	@Asset(Text.class)
	private static final String UPGRADING_MASTER_JSON_PATH = "data/misc/updgrading_master_task.json";

	private PlayerTasks() {
		// shouldn't get instantiated
	}

	public static List<PlayerTask> getValues() {
		return VALUES;
	}

	public static void initialize(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		LEARNING_SKILL = SimpleJSONParser.parseFromJson(assetManager
				.get(LEARNING_AGILITY_JSON_PATH, Text.class).getString(),
				PlayerTask.class);
		VALUES.add(LEARNING_SKILL);

		UPGRADING_MASTER = SimpleJSONParser.parseFromJson(assetManager
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
		/**
		 * The {@linkplain Player#getAvailableAp() AP} it costs to perform this
		 * task in its basic form.
		 */
		private int basicApCost;

		PlayerTask() {
		}

		public int getBasicDuration() {
			return basicDuration;
		}

		public int getBasicApCost() {
			return basicApCost;
		}

	}

}
