package de.gg.game.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.gg.engine.asset.JSON;
import de.gg.engine.asset.JSONLoader.JSONLoaderParameter;
import de.gg.engine.lang.Localizable;

/**
 * This class represents a trait npc characters have. Traits influence the
 * decisions these characters make.
 */
public enum NPCCharacterTrait implements Localizable {
	/**
	 * Values his own benefit higher than the one of others.
	 */
	AMBITIOUS,
	/**
	 * The default trait for npcs.
	 */
	EVEN_TEMPERED,
	/**
	 * A character with this trait tries to do the right thing. Bribes aren't as
	 * effective.
	 */
	RIGHTEOUS,
	/**
	 * Doesn't like heretics.
	 */
	RELIGIOUS_FANATIC;

	public final static String TRAIT_JSON_DIR = "data/traits";

	public AssetDescriptor<JSON> getJSONAssetDescriptor() {
		return new AssetDescriptor<>(
				String.format("%s/%s.json", TRAIT_JSON_DIR,
						this.name().toLowerCase()),
				JSON.class,
				new JSONLoaderParameter(NPCCharacterTraitData.class));
	}

	public NPCCharacterTraitData getData() {
		return TypeRegistry.getInstance().TRAIT_TYPE_DATA.get(this);
	}

	@Override
	public String getUnlocalizedName() {
		return "type.trait." + this.name().toLowerCase() + ".name";
	}

	public class NPCCharacterTraitData {
		/**
		 * The base opinion modifier a character with this trait has of other
		 * characters. Is in the range of <code>0</code> and <code>10</code>.
		 */
		private int baseOpinionModifier;
		/**
		 * The decision modifier in impeachment votes, when the npc is
		 * interested in the impeached position. Is in the range of
		 * <code>-15</code> and <code>0</code>. Lower values denote a
		 * <i>higher</i> ambition.
		 */
		private int ambitionDecisionModifier;
		/**
		 * A general decision modifier to describe whether a character has
		 * scruples to set his own gain above someone else's. This modifier is
		 * for example used to decide whether a npc likes to impeach another
		 * character that he doesn't <i>dis</i>like. Is in the range of
		 * <code>0</code> and <code>8</code>. Higher values denote a higher
		 * scruple.
		 */
		private int generalLoyaltyDecisionModifier;
		/**
		 * Whether the religion of a character is important.
		 */
		private boolean religionIsImportant;

		NPCCharacterTraitData() {
			// default public constructor
		}

		// TODO Modifikatoren für bestimmte Situationen (Bestechung, Erpressung,
		// Anklage, Amtsabwahl, etc.)

		public int getBaseOpinionModifier() {
			return baseOpinionModifier;
		}

		public boolean isReligionImportant() {
			return religionIsImportant;
		}

		public int getAmbitionDecisionModifier() {
			return ambitionDecisionModifier;
		}

		public int getGeneralLoyaltyDecisionModifier() {
			return generalLoyaltyDecisionModifier;
		}

	}
}