package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.type.ItemTypes.ItemType;
import de.gg.game.type.ProfessionTypes.ProfessionType;
import de.gg.util.JSONParser;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public final class BuildingTypes {

	public static BuildingType TOWN_HALL, FORGE_1, FORGE_2;
	private static List<BuildingType> VALUES;

	@Asset(Text.class)
	private static final String TOWN_HALL_JSON_PATH = "data/buildings/town_hall.json";
	@Asset(Text.class)
	private static final String FORGE_1_JSON_PATH = "data/buildings/forge_1.json";
	@Asset(Text.class)
	private static final String FORGE_2_JSON_PATH = "data/buildings/forge_2.json";

	private BuildingTypes() {
	}

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		TOWN_HALL = JSONParser.parseFromJson(
				assetManager.get(TOWN_HALL_JSON_PATH, Text.class).getString(),
				BuildingType.class);
		VALUES.add(TOWN_HALL);
		FORGE_1 = JSONParser.parseFromJson(
				assetManager.get(FORGE_1_JSON_PATH, Text.class).getString(),
				BuildingType.class);
		VALUES.add(FORGE_1);
		FORGE_2 = JSONParser.parseFromJson(
				assetManager.get(FORGE_2_JSON_PATH, Text.class).getString(),
				BuildingType.class);
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

	}

}
