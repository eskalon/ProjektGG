package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.type.ItemTypes.ItemType;
import de.gg.game.type.ProfessionTypes.ProfessionType;
import de.gg.util.asset.JSON;
import de.gg.util.asset.JSONLoader.JSONLoaderParameter;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public final class BuildingTypes {

	public static BuildingType TOWN_HALL, FORGE_1, FORGE_2;
	private static List<BuildingType> VALUES;

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> TOWN_HALL_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/buildings/town_hall.json",
				JSON.class, new JSONLoaderParameter(BuildingType.class));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> FORGE_1_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/buildings/forge_1.json",
				JSON.class, new JSONLoaderParameter(BuildingType.class));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> FORGE_2_JSON_PATH() {
		return new AssetDescriptor<JSON>("data/buildings/forge_2.json",
				JSON.class, new JSONLoaderParameter(BuildingType.class));
	}

	private BuildingTypes() {
		// shouldn't get instantiated
	}

	public static List<BuildingType> getValues() {
		return VALUES;
	}

	public static void initialize(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		TOWN_HALL = assetManager.get(TOWN_HALL_JSON_PATH())
				.getData(BuildingType.class);
		VALUES.add(TOWN_HALL);

		FORGE_1 = assetManager.get(FORGE_1_JSON_PATH())
				.getData(BuildingType.class);
		VALUES.add(FORGE_1);

		FORGE_2 = assetManager.get(FORGE_2_JSON_PATH())
				.getData(BuildingType.class);
		VALUES.add(FORGE_2);
	}

	public static BuildingType getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public class BuildingType {

		private String name;
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

		BuildingType() {
		}

		public String getName() {
			return name;
		}

		public int getMaxHealth() {
			return maxHealth;
		}

		/**
		 * @return the upgraded version of this building. <code>Null</code> if
		 *         this building cannot be upgraded.
		 */
		public BuildingType getUpgradeOption() {
			return getByIndex(upgradeOptionIndex);
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
			return ProfessionTypes.getByIndex(professionTypeIndex);
		}

		/**
		 * @return The items this building can produce. If this building cannot
		 *         produce any items it return null.
		 */
		public ItemType[] getProduciableGoods() {
			if (producibleGoodsIds != null && producibleGoodsIds.length > 0) {
				ItemType[] tmp = new ItemType[producibleGoodsIds.length];
				for (int i = 0; i < producibleGoodsIds.length; i++) {
					tmp[i] = ItemTypes.getByIndex(producibleGoodsIds[i]);
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
		 * @return the cost/value of this building type.
		 */
		public float getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			return Objects.equals(name, ((BuildingType) obj).name);
		}

	}

}
