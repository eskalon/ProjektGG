package de.gg.game.factory;

import java.util.ArrayList;
import java.util.Random;

import com.google.common.reflect.TypeToken;

import de.gg.game.data.GameDifficulty;
import de.gg.game.entity.Character;
import de.gg.game.type.NPCCharacterTraits;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.type.ProfessionTypes.ProfessionType;
import de.gg.game.type.Religion;
import de.gg.game.type.SocialStatusS;
import de.gg.game.type.SocialStatusS.SocialStatus;
import de.gg.util.RandomUtils;
import de.gg.util.asset.Text;
import de.gg.util.json.SimpleJSONParser;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This class is responsible for creating the character entities.
 */
public class CharacterFactory {

	private static ArrayList<String> SURNAMES;
	private static ArrayList<String> MALE_NAMES;
	private static ArrayList<String> FEMALE_NAMES;

	@Asset(Text.class)
	private static final String SURNAMES_JSON_PATH = "data/misc/surnames.json";
	@Asset(Text.class)
	private static final String FEMALE_NAMES_JSON_PATH = "data/misc/female_names.json";
	@Asset(Text.class)
	private static final String MALE_NAMES_JSON_PATH = "data/misc/male_names.json";

	private CharacterFactory() {
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
		c.setAge(RandomUtils.getRandomNumber(random, 17, 60));

		if (status != SocialStatusS.NON_CITIZEN)
			c.setGold(RandomUtils.getRandomNumber(random,
					status.getFortuneRequirement(),
					status.getFortuneRequirement() * 2 - 200));
		else
			c.setGold(RandomUtils.getRandomNumber(random, 135, 735));

		if (status == SocialStatusS.NON_CITIZEN)
			c.setHighestPositionLevel(
					RandomUtils.getRandomNumber(random, 0, 5));
		else if (status == SocialStatusS.CITIZEN)
			c.setHighestPositionLevel(
					RandomUtils.getRandomNumber(random, 0, 3));
		else if (status == SocialStatusS.PATRICIAN)
			c.setHighestPositionLevel(
					RandomUtils.getRandomNumber(random, 1, 6));
		else if (status == SocialStatusS.CAVALIER)
			c.setHighestPositionLevel(
					RandomUtils.getRandomNumber(random, 2, 9));
		else if (status == SocialStatusS.BARON)
			c.setHighestPositionLevel(
					RandomUtils.getRandomNumber(random, 7, 9));

		c.setHp(RandomUtils.getRandomNumber(random, 85, 105));
		c.setMale(RandomUtils.rollTheDice(random, 2));
		c.setMarried(!RandomUtils.rollTheDice(random, 3)); // two thirds of the
		// characters cannot get married

		if (c.isMale()) {
			c.setName(MALE_NAMES.get(RandomUtils.getRandomNumber(random, 0,
					MALE_NAMES.size() - 1)));
		} else {
			c.setName(FEMALE_NAMES.get(RandomUtils.getRandomNumber(random, 0,
					FEMALE_NAMES.size() - 1)));
		}

		c.setReligion(RandomUtils.rollTheDice(random, 2) ? Religion.CATHOLIC
				: Religion.ORTHODOX);
		c.setStatus(status);
		c.setSurname(SURNAMES.get(
				RandomUtils.getRandomNumber(random, 0, SURNAMES.size() - 1)));

		switch (RandomUtils.getRandomNumber(random, 0, 7)) {
		case 0:
		case 1:
		case 2:
		case 3: {
			c.setNPCTrait(NPCCharacterTraits.EVEN_TEMPERED);
			break;
		}
		case 4:
		case 5: {
			c.setNPCTrait(NPCCharacterTraits.AMBITIOUS);
			break;
		}
		case 6: {
			c.setNPCTrait(NPCCharacterTraits.EVEN_TEMPERED);
			break;
		}
		case 7: {
			c.setNPCTrait(NPCCharacterTraits.EVEN_TEMPERED);
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

		if (RandomUtils.rollTheDice(2)) {
			status = SocialStatusS.CITIZEN; // 50%
		} else if (!RandomUtils.rollTheDice(3)) {
			status = SocialStatusS.PATRICIAN; // 33%
		} else if (!RandomUtils.rollTheDice(3)) {
			status = SocialStatusS.CAVALIER; // 11%
		} else if (!RandomUtils.rollTheDice(4)) {
			status = SocialStatusS.BARON; // 4%
		} else {
			status = SocialStatusS.NON_CITIZEN; // 1%
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
			status = SocialStatusS.CITIZEN;
			break;
		}
		case 3: {
			status = RandomUtils.rollTheDice(random, 3)
					? SocialStatusS.PATRICIAN
					: SocialStatusS.CITIZEN;
			break;
		}
		case 4:
		case 5: {
			status = SocialStatusS.PATRICIAN;
			break;
		}
		case 6: {
			status = RandomUtils.rollTheDice(random, 2)
					? SocialStatusS.PATRICIAN
					: SocialStatusS.CAVALIER;
			break;
		}
		case 7: {
			status = SocialStatusS.CAVALIER;
			break;
		}
		case 8: {
			status = RandomUtils.rollTheDice(random, 3) ? SocialStatusS.CAVALIER
					: SocialStatusS.BARON;
			break;
		}
		case 9: {
			status = SocialStatusS.BARON;
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
		c.setAge(RandomUtils.getRandomNumber(random, 17, 23));
		c.setGold(profession.getStartingGold()
				+ difficulty.getAdditionalStartingGold());

		c.setHighestPositionLevel(0);

		c.setHp(RandomUtils.getRandomNumber(random, 96, 104));
		c.setMale(isMale);
		c.setMarried(false);
		c.setName(name);
		c.setReligion(religion);
		c.setStatus(SocialStatusS.NON_CITIZEN);
		c.setSurname(surname);
		c.setNPCTrait(null);

		return c;
	}

	public static void initialize(AnnotationAssetManager assetManager) {
		FEMALE_NAMES = SimpleJSONParser
				.parseFromJson(
						assetManager.get(FEMALE_NAMES_JSON_PATH, Text.class)
								.getString(),
						new TypeToken<ArrayList<String>>() {
						}.getType());
		MALE_NAMES = SimpleJSONParser.parseFromJson(
				assetManager.get(MALE_NAMES_JSON_PATH, Text.class).getString(),
				new TypeToken<ArrayList<String>>() {
				}.getType());
		SURNAMES = SimpleJSONParser.parseFromJson(
				assetManager.get(SURNAMES_JSON_PATH, Text.class).getString(),
				new TypeToken<ArrayList<String>>() {
				}.getType());
	}

}
