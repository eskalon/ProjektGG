package de.gg.game.factory;

import java.util.Random;

import de.gg.game.entity.Character;
import de.gg.game.entity.Character.Religion;
import de.gg.game.entity.NPCCharacterTraits;
import de.gg.game.entity.SocialStatusS;
import de.gg.game.entity.SocialStatusS.SocialStatus;
import de.gg.util.RandomUtils;

public class CharacterFactory {

	private static final String[] SURNAMES = { "Vorname" };
	private static final String[] NAMES = { "Nachname" };

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
		c.setStatus(SocialStatusS.NON_CITIZEN);
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

}
