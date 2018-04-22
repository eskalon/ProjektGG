package de.gg.game.type;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

public class NPCCharacterTraits {

	/**
	 * Values his own benefit higher than the average citizen.
	 */
	public static CharacterTrait AMBITIOUS;
	/**
	 * The default trait.
	 */
	public static CharacterTrait EVEN_TEMPERED;
	/**
	 * Tries to do the right thing. Bribes aren't as effective.
	 */
	public static CharacterTrait RIGHTEOUS;
	/**
	 * Doesn't like heretics.
	 */
	public static CharacterTrait RELIGIOUS_FANATIC;

	private static List<CharacterTrait> VALUES;

	// @Asset(Text.class)
	// private static final String AMBITIOUS_JSON_PATH =
	// "data/traits/ambitious.json";

	private NPCCharacterTraits() {
	}

	public static void finishLoading(AssetManager assetManager) {
		VALUES = new ArrayList<>();

		/*
		 * AMBITIOUS = JSONParser.parseFromJson(assetManager
		 * .get(AMBITIOUS_JSON_PATH, Text.class).getString(),
		 * NPCCharacterTrait.class); VALUES.add(AMBITIOUS);
		 */
	}

	public static CharacterTrait getByIndex(int index) {
		if (index == -1 || index > VALUES.size() - 1)
			return null;
		return VALUES.get(index);
	}

	/**
	 * This class represents a trait npc characters have. Traits influence the
	 * decisions these characters make.
	 */
	public class CharacterTrait {

		/**
		 * The base opinion modifier a character with this trait has of other
		 * characters. Is in the range of <code>0</code> and <code>10</code>.
		 */
		private int baseOpinionModifier;
		/**
		 * Whether the religion of a character is important.
		 */
		private boolean religionIsImportant;

		// TODO Modifikatoren für bestimmte Situationen (Bestechung, Erpressung,
		// Anklage, Amtsabwahl, etc.)

		public int getBaseOpinionModifier() {
			return baseOpinionModifier;
		}

		public boolean isReligionImportant() {
			return religionIsImportant;
		}

	}

}
