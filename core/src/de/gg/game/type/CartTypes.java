package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.util.JSONParser;
import de.gg.util.asset.Text;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

public class CartTypes {

	public static CartType BASIC;
	private static List<CartType> VALUES;

	@Asset(Text.class)
	private static final String BASIC_JSON_PATH = "data/carts/cart_1.json";

	private CartTypes() {
		// shouldn't get instantiated
	}

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		BASIC = JSONParser.parseFromJson(
				assetManager.get(BASIC_JSON_PATH, Text.class).getString(),
				CartType.class);
		VALUES.add(BASIC);
	}

	public static CartType getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	public class CartType {

		private String name;
		private int maxHP;
		private int price;
		/**
		 * Probability that an accident happens.
		 */
		private int accidentRate;
		private int maxStackSize;
		private int maxStackCount;

		CartType() {
		}

		public String getName() {
			return name;
		}

		public int getMaxHP() {
			return maxHP;
		}

		public int getPrice() {
			return price;
		}

		public int getAccidentRate() {
			return accidentRate;
		}

		public int getMaxStackSize() {
			return maxStackSize;
		}

		public int getMaxStackCount() {
			return maxStackCount;
		}

	}
}
