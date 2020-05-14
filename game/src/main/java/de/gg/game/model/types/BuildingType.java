package de.gg.game.model.types;

import javax.annotation.Nullable;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.asset.JSON;
import de.eskalon.commons.asset.JSONLoader.JSONLoaderParameter;
import de.eskalon.commons.lang.ILocalizable;
import de.gg.game.model.entities.Building;

public enum BuildingType implements ILocalizable {
	TOWN_HALL, FORGE_1, FORGE_2;

	public final static String BUILDING_JSON_DIR = "data/buildings";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", BUILDING_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(BuildingTypeData.class));
	}

	private BuildingTypeData getData() {
		return TypeRegistry.getInstance().BUILDING_TYPE_DATA.get(this);
	}

	public int getMaxHealth() {
		return getData().maxHealth;
	}

	/**
	 * @return the upgraded version of this building. {@code null} if this
	 *         building cannot be upgraded.
	 */
	@Nullable
	public BuildingType getUpgradeOption() {
		if (getData().upgradeOptionIndex == -1)
			return null;
		return BuildingType.values()[getData().upgradeOptionIndex];
	}

	/**
	 * @return whether this is a residence and therefore a player can live
	 *         inside this building.
	 */
	public boolean isResidence() {
		return getData().isResidence;
	}

	/**
	 * @return whether this building is a public building. Public buildings can
	 *         not be destroyed.
	 */
	public boolean isPublicBuilding() {
		return !isResidence() && !isProductionBuilding();
	}

	/**
	 * @return whether this is a production building. If that is the case
	 *         {@link #getType()} returns the profession this building belongs
	 *         to.
	 */
	public boolean isProductionBuilding() {
		return getData().professionTypeIndex > -1;
	}

	public ProfessionType getType() {
		if (getData().professionTypeIndex == -1)
			return null;
		return ProfessionType.values()[getData().professionTypeIndex];
	}

	/**
	 * @return The items this building can produce. If this building cannot
	 *         produce any items it returns {@cod null}.
	 */
	@Nullable
	public ItemType[] getProduciableGoods() {
		if (getData().producibleGoodsIds != null
				&& getData().producibleGoodsIds.length > 0) {
			ItemType[] tmp = new ItemType[getData().producibleGoodsIds.length];
			for (int i = 0; i < getData().producibleGoodsIds.length; i++) {
				tmp[i] = ItemType.values()[getData().producibleGoodsIds[i]];
			}
			return tmp;
		}
		return null;
	}

	public int getMaxStackSize() {
		return getData().maxStackSize;
	}

	public int getItemSlotCount() {
		return getData().itemSlotCount;
	}

	public String getModelPath() {
		return getData().modelPath;
	}

	/**
	 * @return the cost/monetary value of this building type.
	 * @see Building#getValue()
	 */
	public float getValue() {
		return getData().value;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.building." + this.name().toLowerCase() + ".name";
	}

	public class BuildingTypeData {
		private String modelPath;

		private int value;

		private int maxHealth;
		private int upgradeOptionIndex = -1;
		/**
		 * Whether this building type is a residential building.
		 */
		private boolean isResidence;

		/**
		 * The profession this production building belongs to.
		 */
		private int professionTypeIndex;
		private int[] producibleGoodsIds;

		private int itemSlotCount;
		private int maxStackSize;

		BuildingTypeData() {
			// default public constructor
		}
	}
}