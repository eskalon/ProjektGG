package de.gg.game.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.lang.Localizable;
import de.gg.utils.asset.JSON;
import de.gg.utils.asset.JSONLoader.JSONLoaderParameter;

public enum BuildingType implements Localizable {
	TOWN_HALL, FORGE_1, FORGE_2;

	public final static String BUILDING_JSON_DIR = "data/buildings";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", BUILDING_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class, new JSONLoaderParameter(BuildingTypeData.class));
	}

	public BuildingTypeData getData() {
		return TypeRegistry.getInstance().BUILDING_TYPE_DATA.get(this);
	}

	@Override
	public String getUnlocalizedName() {
		return "type.building." + this.name().toLowerCase() + ".name";
	}

	public class BuildingTypeData {
		private String nodeName;

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

		private int maxStackSize;
		private int maxStackCount;

		BuildingTypeData() {
			// default public constructor
		}

		public int getMaxHealth() {
			return maxHealth;
		}

		/**
		 * @return the upgraded version of this building. <code>Null</code> if
		 *         this building cannot be upgraded.
		 */
		public BuildingType getUpgradeOption() {
			if (upgradeOptionIndex == -1)
				return null;
			return BuildingType.values()[upgradeOptionIndex];
		}

		/**
		 * @return whether this is a residence and therefore a player can live
		 *         inside this building.
		 */
		public boolean isResidence() {
			return isResidence;
		}

		/**
		 * @return whether this building is a public building. Public buildings
		 *         can not be destroyed.
		 */
		public boolean isPublicBuilding() {
			return !isResidence() && !isProductionBuilding();
		}

		/**
		 * @return whether this is a production building. If that is the case
		 *         {@link #getType()} returns the profession this building
		 *         belongs to.
		 */
		public boolean isProductionBuilding() {
			return professionTypeIndex > -1;
		}

		public ProfessionType getType() {
			if (professionTypeIndex == -1)
				return null;
			return ProfessionType.values()[professionTypeIndex];
		}

		/**
		 * @return The items this building can produce. If this building cannot
		 *         produce any items it return null.
		 */
		public ItemType[] getProduciableGoods() {
			if (producibleGoodsIds != null && producibleGoodsIds.length > 0) {
				ItemType[] tmp = new ItemType[producibleGoodsIds.length];
				for (int i = 0; i < producibleGoodsIds.length; i++) {
					tmp[i] = ItemType.values()[producibleGoodsIds[i]];
				}
				return tmp;
			}
			return null;
		}

		public int getMaxStackSize() {
			return maxStackSize;
		}

		public int getMaxStackCount() {
			return maxStackCount;
		}

		/**
		 * @return the name of the node that holds the model for this building
		 *         type.
		 */
		public String getNodeName() {
			return nodeName;
		}

		/**
		 * @return the cost/monetary value of this building type.
		 */
		public float getValue() {
			return value;
		}
	}
}