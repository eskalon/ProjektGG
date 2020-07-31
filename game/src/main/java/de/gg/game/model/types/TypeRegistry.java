package de.gg.game.model.types;

import java.util.ArrayList;
import java.util.EnumMap;

import com.badlogic.gdx.assets.AssetManager;

import de.gg.game.model.types.BuildingType.BuildingTypeData;
import de.gg.game.model.types.CartType.CartTypeData;
import de.gg.game.model.types.CrimeType.CrimeTypeData;
import de.gg.game.model.types.GameMap.GameMapData;
import de.gg.game.model.types.ItemType.ItemTypeData;
import de.gg.game.model.types.LawType.LawTypeData;
import de.gg.game.model.types.NPCCharacterTrait.NPCCharacterTraitData;
import de.gg.game.model.types.PlayerTaskType.PlayerTaskTypeData;
import de.gg.game.model.types.PositionType.PositionTypeData;
import de.gg.game.model.types.ProfessionType.ProfessionTypeData;
import de.gg.game.model.types.SocialStatus.SocialStatusData;

public class TypeRegistry {

	private static TypeRegistry instance = new TypeRegistry();

	public final EnumMap<BuildingType, BuildingTypeData> BUILDING_TYPE_DATA = new EnumMap<>(
			BuildingType.class);
	public final EnumMap<CartType, CartTypeData> CART_TYPE_DATA = new EnumMap<>(
			CartType.class);
	public final EnumMap<CrimeType, CrimeTypeData> CRIME_TYPE_DATA = new EnumMap<>(
			CrimeType.class);
	public final EnumMap<GameMap, GameMapData> MAP_TYPE_DATA = new EnumMap<>(
			GameMap.class);
	public final EnumMap<ItemType, ItemTypeData> ITEM_TYPE_DATA = new EnumMap<>(
			ItemType.class);
	public final EnumMap<LawType, LawTypeData> LAW_TYPE_DATA = new EnumMap<>(
			LawType.class);
	public final EnumMap<NPCCharacterTrait, NPCCharacterTraitData> TRAIT_TYPE_DATA = new EnumMap<>(
			NPCCharacterTrait.class);
	public final EnumMap<PlayerTaskType, PlayerTaskTypeData> TASK_TYPE_DATA = new EnumMap<>(
			PlayerTaskType.class);
	public final EnumMap<PositionType, PositionTypeData> POSITION_TYPE_DATA = new EnumMap<>(
			PositionType.class);
	public final EnumMap<ProfessionType, ProfessionTypeData> PROFESSION_TYPE_DATA = new EnumMap<>(
			ProfessionType.class);
	public final EnumMap<SocialStatus, SocialStatusData> SOCIAL_STATUS_TYPE_DATA = new EnumMap<>(
			SocialStatus.class);

	private TypeRegistry() {
		// is a singleton
	}

	public static TypeRegistry getInstance() {
		return instance;
	}

	public void initialize(AssetManager assetManager) {
		for (BuildingType t : BuildingType.values()) {
			BUILDING_TYPE_DATA.put(t,
					assetManager.get(t.getJSONAssetDescriptor())
							.getData(BuildingTypeData.class));
		}
		// for (CartType t : CartType.values()) {
		// CART_TYPE_DATA.put(t, assetManager.get(t.getJSONAssetDescriptor())
		// .getData(CartTypeData.class));
		// }
		// for (CrimeType t : CrimeType.values()) {
		// CRIME_TYPE_DATA.put(t, assetManager.get(t.getJSONAssetDescriptor())
		// .getData(CrimeTypeData.class));
		// }
		for (GameMap t : GameMap.values()) {
			MAP_TYPE_DATA.put(t, assetManager.get(t.getJSONAssetDescriptor())
					.getData(GameMapData.class));
		}
		// for (ItemType t : ItemType.values()) {
		// ITEM_TYPE_DATA.put(t, assetManager.get(t.getJSONAssetDescriptor())
		// .getData(ItemTypeData.class));
		// }
		for (LawType t : LawType.values()) {
			LAW_TYPE_DATA.put(t, assetManager.get(t.getJSONAssetDescriptor())
					.getData(LawTypeData.class));
			LAW_TYPE_DATA.get(t).setVoters(new ArrayList<>());
		}
		for (NPCCharacterTrait t : NPCCharacterTrait.values()) {
			TRAIT_TYPE_DATA.put(t, assetManager.get(t.getJSONAssetDescriptor())
					.getData(NPCCharacterTraitData.class));
		}
		for (PlayerTaskType t : PlayerTaskType.values()) {
			TASK_TYPE_DATA.put(t, assetManager.get(t.getJSONAssetDescriptor())
					.getData(PlayerTaskTypeData.class));
		}
		for (PositionType t : PositionType.values()) {
			POSITION_TYPE_DATA.put(t,
					assetManager.get(t.getJSONAssetDescriptor())
							.getData(PositionTypeData.class));

			if (t.hasLawsToVoteFor()) {
				for (Integer i : t.getIndicesOfLawsToVoteFor()) {
					LawType.values()[i].getVoters().add(t);
				}
			}
		}
		for (ProfessionType t : ProfessionType.values()) {
			PROFESSION_TYPE_DATA.put(t,
					assetManager.get(t.getJSONAssetDescriptor())
							.getData(ProfessionTypeData.class));
		}
		for (SocialStatus t : SocialStatus.values()) {
			SOCIAL_STATUS_TYPE_DATA.put(t,
					assetManager.get(t.getJSONAssetDescriptor())
							.getData(SocialStatusData.class));
		}
	}

}
