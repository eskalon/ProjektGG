package de.gg.game.factory;

import java.util.Random;

import de.gg.game.data.GameDifficulty;
import de.gg.game.entity.Character;
import de.gg.game.type.NPCCharacterTraits;
import de.gg.game.type.ProfessionTypes.ProfessionType;
import de.gg.game.type.Religion;
import de.gg.game.type.SocialStatusS;
import de.gg.game.type.SocialStatusS.SocialStatus;
import de.gg.util.RandomUtils;

/**
 * This class is responsible for creating the character entities.
 */
public class CharacterFactory {

	private static final String[] SURNAMES = { "Vorname" };
	private static final String[] NAMES = { "Nachname" };

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
		c.setGold(RandomUtils.getRandomNumber(random,
				status.getFortuneRequirement(),
				status.getFortuneRequirement() * 2 - 200));

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
		// characters cannot get
		// married
		c.setName(NAMES[RandomUtils.getRandomNumber(random, 0,
				NAMES.length - 1)]);
		c.setReligion(RandomUtils.rollTheDice(random, 2) ? Religion.CATHOLIC
				: Religion.ORTHODOX);
		c.setStatus(status);
		c.setSurname(SURNAMES[RandomUtils.getRandomNumber(random, 0,
				SURNAMES.length - 1)]);

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

}
