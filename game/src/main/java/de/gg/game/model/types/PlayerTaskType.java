package de.gg.game.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.gg.game.asset.JSON;
import de.gg.game.asset.JSONLoader.JSONLoaderParameter;
import de.gg.game.model.entities.Player;

public enum PlayerTaskType implements ILocalizable {
	LEARNING_SKILL, UPGRADING_MASTER;

	public final static String TASK_JSON_DIR = "data/misc/tasks";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", TASK_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(PlayerTaskTypeData.class));
	}

	private PlayerTaskTypeData getData() {
		return TypeRegistry.getInstance().TASK_TYPE_DATA.get(this);
	}

	public int getBasicDuration() {
		return getData().basicDuration;
	}

	public int getBasicApCost() {
		return getData().basicApCost;
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
		 * The {@linkplain Player#getAvailableAP() AP} it costs to perform this
		 * task in its basic form.
		 */
		private int basicApCost;

		PlayerTaskTypeData() {
			// default public constructor
		}
	}

}
