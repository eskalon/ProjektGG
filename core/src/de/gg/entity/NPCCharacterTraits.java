package de.gg.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

public class NPCCharacterTraits {

	/**
	 * Values his own benefit higher than the average citizen.
	 */
	public CharacterTrait AMBITIOUS;
	/**
	 * The default trait.
	 */
	public CharacterTrait EVEN_TEMPERED;
	/**
	 * Tries to do the right thing. Bribes aren't as effective.
	 */
	public CharacterTrait RIGHTEOUS;
	/**
	 * Doesn't like heretics.
	 */
	public CharacterTrait RELIGIOUS_FANATIC;

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

		// TODO Modifikatoren für bestimmte Situationen

	}

}
