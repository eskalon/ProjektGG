package de.eskalon.gg.simulation.model.factories;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.reflect.TypeToken;

import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.utils.RandomUtils;
import de.eskalon.gg.asset.JSON;
import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.types.GameDifficulty;
import de.eskalon.gg.simulation.model.types.NPCCharacterTrait;
import de.eskalon.gg.simulation.model.types.PositionType;
import de.eskalon.gg.simulation.model.types.ProfessionType;
import de.eskalon.gg.simulation.model.types.Religion;
import de.eskalon.gg.simulation.model.types.SocialStatus;

/**
 * This class is responsible for creating the {@link Character} entities.
 */
public class CharacterFactory {

	private static Type TYPE = new TypeToken<ArrayList<String>>() {
	}.getType();

	@Asset(value = "data/misc/surnames.json", params = "array_list_string")
	private static JSON SURNAMES_ASSET;
	@Asset(value = "data/misc/male_names.json", params = "array_list_string")
	private static JSON MALE_NAMES_ASSET;
	@Asset(value = "data/misc/female_names.json", params = "array_list_string")
	private static JSON FEMALE_NAMES_ASSET;

	private static ArrayList<String> SURNAMES;
	private static ArrayList<String> MALE_NAMES;
	private static ArrayList<String> FEMALE_NAMES;

	private CharacterFactory() {
		// not used
	}

	/**
	 * Creates a random non-player character specified by its social status.
	 *
	 * @param random
	 *            The random generator for this session.
	 * @param status
	 *            The social status of this character. Is used as guideline for
	 *            all other values.
	 * @return a random non-player character.
	 */
	public static Character createCharacterWithStatus(Random random,
			SocialStatus status) {
		Character c = new Character();
		c.setAge(RandomUtils.getInt(random, 17, 60));

		if (status != SocialStatus.NON_CITIZEN)
			c.setGold(RandomUtils.getInt(random, status.getFortuneRequirement(),
					status.getFortuneRequirement() * 2 - 200));
		else
			c.setGold(RandomUtils.getInt(random, 135, 735));

		if (status == SocialStatus.NON_CITIZEN)
			c.setHighestPositionLevel(RandomUtils.getInt(random, 0, 5));
		else if (status == SocialStatus.CITIZEN)
			c.setHighestPositionLevel(RandomUtils.getInt(random, 0, 3));
		else if (status == SocialStatus.PATRICIAN)
			c.setHighestPositionLevel(RandomUtils.getInt(random, 1, 6));
		else if (status == SocialStatus.CAVALIER)
			c.setHighestPositionLevel(RandomUtils.getInt(random, 2, 9));
		else if (status == SocialStatus.BARON)
			c.setHighestPositionLevel(RandomUtils.getInt(random, 7, 9));

		c.setHp(RandomUtils.getInt(random, 85, 105));
		c.setMale(RandomUtils.isTrue(random, 2));
		c.setMarried(!RandomUtils.isTrue(random, 3)); // two thirds of the
		// characters cannot get married

		if (c.isMale()) {
			c.setName(MALE_NAMES
					.get(RandomUtils.getInt(random, 0, MALE_NAMES.size() - 1)));
		} else {
			c.setName(FEMALE_NAMES.get(
					RandomUtils.getInt(random, 0, FEMALE_NAMES.size() - 1)));
		}

		c.setReligion(RandomUtils.isTrue(random, 2) ? Religion.CATHOLIC
				: Religion.ORTHODOX);
		c.setStatus(status);
		c.setSurname(SURNAMES
				.get(RandomUtils.getInt(random, 0, SURNAMES.size() - 1)));

		switch (RandomUtils.getInt(random, 0, 7)) {
		case 0:
		case 1:
		case 2:
		case 3: {
			c.setNpcTrait(NPCCharacterTrait.EVEN_TEMPERED);
			break;
		}
		case 4:
		case 5: {
			c.setNpcTrait(NPCCharacterTrait.AMBITIOUS);
			break;
		}
		case 6: {
			c.setNpcTrait(NPCCharacterTrait.EVEN_TEMPERED);
			break;
		}
		case 7: {
			c.setNpcTrait(NPCCharacterTrait.EVEN_TEMPERED);
			break;
		}
		}

		return c;
	}

	/**
	 * Creates a random non-player character.
	 *
	 * @param random
	 *            The random generator for this session.
	 * @return a random non-player character.
	 * @see #createCharacterWithStatus(Random, SocialStatus)
	 */
	public static Character createRandomCharacter(Random random) {
		SocialStatus status;

		if (RandomUtils.isTrue(2)) {
			status = SocialStatus.CITIZEN; // 50%
		} else if (!RandomUtils.isTrue(3)) {
			status = SocialStatus.PATRICIAN; // 33%
		} else if (!RandomUtils.isTrue(3)) {
			status = SocialStatus.CAVALIER; // 11%
		} else if (!RandomUtils.isTrue(4)) {
			status = SocialStatus.BARON; // 4%
		} else {
			status = SocialStatus.NON_CITIZEN; // 1%
		}

		return createCharacterWithStatus(random, status);
	}

	public static Character createCharacterForPosition(Random random,
			PositionType posType) {
		SocialStatus status;

		switch (posType.getLevel()) {
		default:
		case 1:
		case 2: {
			status = SocialStatus.CITIZEN;
			break;
		}
		case 3: {
			status = RandomUtils.isTrue(random, 3) ? SocialStatus.PATRICIAN
					: SocialStatus.CITIZEN;
			break;
		}
		case 4:
		case 5: {
			status = SocialStatus.PATRICIAN;
			break;
		}
		case 6: {
			status = RandomUtils.isTrue(random, 2) ? SocialStatus.PATRICIAN
					: SocialStatus.CAVALIER;
			break;
		}
		case 7: {
			status = SocialStatus.CAVALIER;
			break;
		}
		case 8: {
			status = RandomUtils.isTrue(random, 3) ? SocialStatus.CAVALIER
					: SocialStatus.BARON;
			break;
		}
		case 9: {
			status = SocialStatus.BARON;
			break;
		}
		}

		Character c = createCharacterWithStatus(random, status);
		c.setHighestPositionLevel(posType.getLevel());
		c.setPosition(posType);

		return c;
	}

	/**
	 * Creates a character for a player.
	 *
	 * @param random
	 *            The random generator for this session.
	 * @param profession
	 *            The player's profession.
	 * @param difficulty
	 *            The game's difficulty level.
	 * @return the character.
	 */
	public static Character createPlayerCharacter(Random random,
			ProfessionType profession, GameDifficulty difficulty,
			boolean isMale, Religion religion, String name, String surname) {
		Character c = new Character();
		c.setAge(RandomUtils.getInt(random, 17, 23));
		c.setGold(profession.getStartingGold()
				+ difficulty.getAdditionalStartingGold());

		c.setHighestPositionLevel(0);

		c.setHp(RandomUtils.getInt(random, 96, 104));
		c.setMale(isMale);
		c.setMarried(false);
		c.setName(name);
		c.setReligion(religion);
		c.setStatus(SocialStatus.NON_CITIZEN);
		c.setSurname(surname);
		c.setNpcTrait(null);

		return c;
	}

	public static void initialize(AnnotationAssetManager assetManager) {
		assetManager.injectAssets(CharacterFactory.class);

		FEMALE_NAMES = FEMALE_NAMES_ASSET.getData(TYPE);
		MALE_NAMES = MALE_NAMES_ASSET.getData(TYPE);
		SURNAMES = SURNAMES_ASSET.getData(TYPE);
	}

}
