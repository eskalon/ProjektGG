package de.gg.game.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.game.entities.Player;
import de.gg.lang.Localizable;
import de.gg.utils.asset.JSON;
import de.gg.utils.asset.JSONLoader.JSONLoaderParameter;

public enum PlayerTaskType implements Localizable {
	LEARNING_SKILL, UPGRADING_MASTER;

	public final static String TASK_JSON_DIR = "data/misc/tasks";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", TASK_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(PlayerTaskTypeData.class));
	}

	public PlayerTaskTypeData getData() {
		return TypeRegistry.getInstance().TASK_TYPE_DATA.get(this);
	}

	@Override
	public String getUnlocalizedName() {
		return "type.task." + this.name().toLowerCase() + ".name";
	}

	public class PlayerTaskTypeData {

		/**
		 * The duration it takes to perform this task in its basic form.
		 */
		private int basicDuration;
		/**
		 * The {@linkplain Player#getAvailableAp() AP} it costs to perform this
		 * task in its basic form.
		 */
		private int basicApCost;

		PlayerTaskTypeData() {
			// default public constructor
		}

		public int getBasicDuration() {
			return basicDuration;
		}

		public int getBasicApCost() {
			return basicApCost;
		}
	}

}
