package de.eskalon.gg.simulation.model.types;

import com.badlogic.gdx.assets.AssetDescriptor;

import de.eskalon.commons.lang.ILocalizable;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.asset.JSONLoader.JSONLoaderParameter;

/**
 * This class represents a trait npc characters have. Traits influence the
 * decisions these characters make.
 */
public enum NPCCharacterTrait implements ILocalizable {
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

	private NPCCharacterTraitData getData() {
		return TypeRegistry.instance().TRAIT_TYPE_DATA.get(this);
	}

	public int getBaseOpinionModifier() {
		return getData().baseOpinionModifier;
	}

	public boolean isReligionImportant() {
		return getData().religionIsImportant;
	}

	public int getAmbitionDecisionModifier() {
		return getData().ambitionDecisionModifier;
	}

	public int getGeneralLoyaltyDecisionModifier() {
		return getData().generalLoyaltyDecisionModifier;
	}

	@Override
	public String getUnlocalizedName() {
		return "type.trait." + this.name().toLowerCase() + ".name";
	}

	public class NPCCharacterTraitData {
		/**
		 * The base opinion modifier a character with this trait has of other
		 * characters. Is in the range of {@code 0} and {@code 10}.
		 */
		private int baseOpinionModifier;
		/**
		 * The decision modifier in impeachment votes, when the npc is
		 * interested in the impeached position. Is in the range of {@code -15}
		 * and {@code 0}. Lower values denote a <i>higher</i> ambition.
		 */
		private int ambitionDecisionModifier;
		/**
		 * A general decision modifier to describe whether a character has
		 * scruples to set his own gain above someone else's. This modifier is
		 * for example used to decide whether a npc likes to impeach another
		 * character that he doesn't <i>dis</i>like. Is in the range of
		 * {@code 0} and {@code 8}. Higher values denote a higher scruple.
		 */
		private int generalLoyaltyDecisionModifier;
		/**
		 * Whether the religion of a character is important.
		 */
		private boolean religionIsImportant;

		NPCCharacterTraitData() {
			// default public constructor
		}

		// TODO modifiers for certain situations (bribery, blackmail, pressing
		// charges, voting out of office, etc.)

	}
}