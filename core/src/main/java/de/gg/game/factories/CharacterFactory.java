package de.gg.game.factories;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.google.common.reflect.TypeToken;

import de.gg.game.entities.Character;
import de.gg.game.types.GameDifficulty;
import de.gg.game.types.NPCCharacterTrait;
import de.gg.game.types.PositionType;
import de.gg.game.types.ProfessionType;
import de.gg.game.types.Religion;
import de.gg.game.types.SocialStatus;
import de.gg.utils.RandomUtils;
import de.gg.utils.asset.JSON;
import de.gg.utils.asset.JSONLoader.JSONLoaderParameter;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * This class is responsible for creating the {@link Character} entities.
 */
public class CharacterFactory {

	private static ArrayList<String> SURNAMES;
	private static ArrayList<String> MALE_NAMES;
	private static ArrayList<String> FEMALE_NAMES;

	private static Type TYPE = new TypeToken<ArrayList<String>>() {
	}.getType();

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> SURNAMES_JSON_PATH() {
		return new AssetDescriptor<>("data/misc/surnames.json", JSON.class,
				new JSONLoaderParameter(TYPE));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> FEMALE_NAMES_JSON_PATH() {
		return new AssetDescriptor<>("data/misc/female_names.json", JSON.class,
				new JSONLoaderParameter(TYPE));
	}

	@Asset(JSON.class)
	public static final AssetDescriptor<JSON> MALE_NAMES_JSON_PATH() {
		return new AssetDescriptor<>("data/misc/male_names.json", JSON.class,
				new JSONLoaderParameter(TYPE));
	}

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
		c.setAge(RandomUtils.getRandomNumber(random, 17, 60));

		if (status != SocialStatus.NON_CITIZEN)
			c.setGold(RandomUtils.getRandomNumber(random,
					status.getData().getFortuneRequirement(),
					status.getData().getFortuneRequirement() * 2 - 200));
		else
			c.setGold(RandomUtils.getRandomNumber(random, 135, 735));

		if (status == SocialStatus.NON_CITIZEN)
			c.setHighestPositionLevel(
					RandomUtils.getRandomNumber(random, 0, 5));
		else if (status == SocialStatus.CITIZEN)
			c.setHighestPositionLevel(
					RandomUtils.getRandomNumber(random, 0, 3));
		else if (status == SocialStatus.PATRICIAN)
			c.setHighestPositionLevel(
					RandomUtils.getRandomNumber(random, 1, 6));
		else if (status == SocialStatus.CAVALIER)
			c.setHighestPositionLevel(
					RandomUtils.getRandomNumber(random, 2, 9));
		else if (status == SocialStatus.BARON)
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
			c.setNPCTrait(NPCCharacterTrait.EVEN_TEMPERED);
			break;
		}
		case 4:
		case 5: {
			c.setNPCTrait(NPCCharacterTrait.AMBITIOUS);
			break;
		}
		case 6: {
			c.setNPCTrait(NPCCharacterTrait.EVEN_TEMPERED);
			break;
		}
		case 7: {
			c.setNPCTrait(NPCCharacterTrait.EVEN_TEMPERED);
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
			status = SocialStatus.CITIZEN; // 50%
		} else if (!RandomUtils.rollTheDice(3)) {
			status = SocialStatus.PATRICIAN; // 33%
		} else if (!RandomUtils.rollTheDice(3)) {
			status = SocialStatus.CAVALIER; // 11%
		} else if (!RandomUtils.rollTheDice(4)) {
			status = SocialStatus.BARON; // 4%
		} else {
			status = SocialStatus.NON_CITIZEN; // 1%
		}

		return createCharacterWithStatus(random, status);
	}

	public static Character createCharacterForPosition(Random random,
			PositionType posType) {
		SocialStatus status;

		switch (posType.getData().getLevel()) {
		default:
		case 1:
		case 2: {
			status = SocialStatus.CITIZEN;
			break;
		}
		case 3: {
			status = RandomUtils.rollTheDice(random, 3) ? SocialStatus.PATRICIAN
					: SocialStatus.CITIZEN;
			break;
		}
		case 4:
		case 5: {
			status = SocialStatus.PATRICIAN;
			break;
		}
		case 6: {
			status = RandomUtils.rollTheDice(random, 2) ? SocialStatus.PATRICIAN
					: SocialStatus.CAVALIER;
			break;
		}
		case 7: {
			status = SocialStatus.CAVALIER;
			break;
		}
		case 8: {
			status = RandomUtils.rollTheDice(random, 3) ? SocialStatus.CAVALIER
					: SocialStatus.BARON;
			break;
		}
		case 9: {
			status = SocialStatus.BARON;
			break;
		}
		}

		Character c = createCharacterWithStatus(random, status);
		c.setHighestPositionLevel(posType.getData().getLevel());
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
		c.setGold(profession.getData().getStartingGold()
				+ difficulty.getAdditionalStartingGold());

		c.setHighestPositionLevel(0);

		c.setHp(RandomUtils.getRandomNumber(random, 96, 104));
		c.setMale(isMale);
		c.setMarried(false);
		c.setName(name);
		c.setReligion(religion);
		c.setStatus(SocialStatus.NON_CITIZEN);
		c.setSurname(surname);
		c.setNPCTrait(null);

		return c;
	}

	public static void initialize(AnnotationAssetManager assetManager) {
		FEMALE_NAMES = assetManager.get(FEMALE_NAMES_JSON_PATH()).getData(TYPE);
		MALE_NAMES = assetManager.get(MALE_NAMES_JSON_PATH()).getData(TYPE);
		SURNAMES = assetManager.get(SURNAMES_JSON_PATH()).getData(TYPE);
	}

}
