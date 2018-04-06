package de.gg.entity;

import java.util.List;

import de.gg.entity.SocialStatusS.SocialStatus;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class PlayerTasks {

	public static PlayerTask LEARNING_SKILL, UPGRADING_MASTER;
	private static List<SocialStatus> VALUES;

	@Asset(Text.class)
	private static final String LEARNING_AGILITY_JSON_PATH = "data/misc/learning_skill_task.json";
	@Asset(Text.class)
	private static final String UPGRADING_MASTER_JSON_PATH = "data/misc/updgrading_master_task.json";

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
