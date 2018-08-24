package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.entity.Player;
import de.gg.util.asset.JSON;
import de.gg.util.asset.JSONLoader.JSONLoaderParameter;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class PlayerTasks {

	public static PlayerTask LEARNING_SKILL, UPGRADING_MASTER;
	private static List<PlayerTask> VALUES;

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> LEARNING_AGILITY_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/misc/learning_skill_task.json",
				JSON.class, new JSONLoaderParameter(PlayerTask.class));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> UPGRADING_MASTER_JSON_PATH() {
		return new AssetDescriptor<JSON>(
				"data/misc/updgrading_master_task.json", JSON.class,
				new JSONLoaderParameter(PlayerTask.class));
	}

	private PlayerTasks() {
		// shouldn't get instantiated
	}

	public static List<PlayerTask> getValues() {
		return VALUES;
	}

	public static void initialize(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		LEARNING_SKILL = assetManager.get(LEARNING_AGILITY_JSON_PATH())
				.getData(PlayerTask.class);
		VALUES.add(LEARNING_SKILL);

		UPGRADING_MASTER = assetManager.get(UPGRADING_MASTER_JSON_PATH())
				.getData(PlayerTask.class);
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
