package de.eskalon.gg.simulation.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.asset.JSONLoader.JSONLoaderParameter;
import de.eskalon.gg.simulation.model.entities.Player;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
		return TypeRegistry.instance().TASK_TYPE_DATA.get(this);
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

	@NoArgsConstructor(access = AccessLevel.PACKAGE)
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
	}

}
