package de.gg.game.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import de.gg.engine.utils.CollectionUtils;
import de.gg.engine.utils.RandomUtils;
import de.gg.game.entities.Character;
import de.gg.game.session.GameSession;
import de.gg.game.types.NPCCharacterTrait;
import de.gg.game.votes.ElectionVote;
import de.gg.game.votes.ImpeachmentVote;
import de.gg.game.votes.VoteOption;
import de.gg.game.votes.VoteableMatter;

/**
 * This class is responsible for simulating a character's actions.
 */
public class CharacterBehaviour {

	private CharacterBehaviour() {
		// not used
	}

	/**
	 * The opinion <code>otherCharacter</code> has of
	 * <code>thisCharacter</code>.
	 * <p>
	 * Has to get compared with the specific skill and trait modifiers as well
	 * as the own usefulness and threat modifiers to determine whether a npc
	 * should execute a specific action.
	 *
	 * @param thisCharacterId
	 *            The id of the character that the opinion is held of.
	 * @param otherCharacterId
	 *            The id of the character that has the opinion.
	 * @param session
	 *            The game session.
	 * @return the opinion <code>otherCharacter</code> has of
	 *         <code>thisCharacter</code>. Is never lower than <code>0</code>
	 *         and normally around <code>55</code>.
	 */
	public static int getOpinionOfAnotherCharacter(short thisCharacterId,
			short otherCharacterId, GameSession session) {
		Character thisCharacter = session.getWorld()
				.getCharacter(thisCharacterId);
		Character otherCharacter = session.getWorld()
				.getCharacter(otherCharacterId);

		int opinion = 0;

		// Difficulty (-4, 10)
		opinion += session.getSessionSetup().getDifficulty()
				.getOpinionModifer();

		// Base Opinion (14, 43)
		Random r = new Random(thisCharacterId * otherCharacterId);
		opinion += RandomUtils.getRandomNumber(r, -9, 20) + 23;

		// NPC Opinion Modifier (0, 10)
		if (otherCharacter.getNPCTrait() != null)
			opinion += otherCharacter.getNPCTrait().getData()
					.getBaseOpinionModifier();

		// Reputation (0, 20)
		opinion += thisCharacter.getReputation();

		// TODO Kinship
		// +20 für Kinder, Eltern und Ehepartner
		// +8 für Verwandte des Ehepartners

		// Religion (5, 12)
		if (otherCharacter.getNPCTrait() != null) {
			boolean isReligionImportant = otherCharacter.getNPCTrait().getData()
					.isReligionImportant();
			if (thisCharacter.getReligion() == otherCharacter.getReligion())
				opinion += isReligionImportant ? 16 : 11;
			else
				opinion += isReligionImportant ? 0 : 5;
		}

		// Temporary Opinion Modifiers
		if (thisCharacter.getOpinionModifiers().containsKey(otherCharacterId))
			opinion += Math.round(
					thisCharacter.getOpinionModifiers().get(otherCharacterId));

		// Temporary Round Modifier (-3, 4)
		opinion += getPerRoundAndCharacterPopularityModifier(
				session.getRandomSeedForCurrentRound(), thisCharacterId,
				otherCharacterId);

		return opinion < 0 ? 0 : opinion;
	}

	/**
	 * Returns the popularity modifier of a character towards another character
	 * in this specific round.
	 *
	 * @param seed
	 *            The random seed for this round.
	 * @param characterIdA
	 *            The id of character a.
	 * @param characterIdB
	 *            The id of character b.
	 * @return The popularity modifier of character a towards character b in
	 *         this specific round.
	 */
	public static int getPerRoundAndCharacterPopularityModifier(long seed,
			short characterIdA, short characterIdB) {
		Random r = new Random(seed * characterIdA * characterIdB);
		return RandomUtils.getRandomNumber(r, -3, 4);
	}

	public static int getVoteOption(short characterId, VoteableMatter matter,
			GameSession session) {
		if (matter instanceof ElectionVote)
			return getElectionVoteOption(characterId, (ElectionVote) matter,
					session);
		else // Impeachment Vote
			return getImpeachmentVoteOption(characterId,
					(ImpeachmentVote) matter, session);
	}

	private static int getImpeachmentVoteOption(short characterId,
			ImpeachmentVote matter, GameSession session) {
		int tmp = -20;
		short characterToImpeachId = matter.getPos().getCurrentHolder();
		Character character = session.getWorld().getCharacter(characterId);
		NPCCharacterTrait trait = character.getNPCTrait();

		if (characterId == characterToImpeachId) {
			tmp = 150;
		} else {
			int opinion = getOpinionOfAnotherCharacter(characterToImpeachId,
					characterId, session);
			tmp += opinion;
			int posLevel = matter.getType().getData().getLevel();
			if (trait != null) {
				// Ambition
				if (character.getPosition().getData().getLevel()
						+ 1 == posLevel)
					tmp += trait.getData().getAmbitionDecisionModifier() - 15;

				// Loyalty
				if (opinion >= 55)
					tmp += trait.getData().getGeneralLoyaltyDecisionModifier();
			}
		}

		return tmp < 0 ? characterToImpeachId : -1;
	}

	@SuppressWarnings("unchecked")
	private static int getElectionVoteOption(short characterId,
			ElectionVote matter, GameSession session) {
		Map<Integer, Integer> options = new HashMap<>();

		for (VoteOption vo : matter.getOptions()) {
			int tmp = 0;
			short voteOptionCharId = (short) vo.getValue();

			tmp += getOpinionOfAnotherCharacter(voteOptionCharId, characterId,
					session);

			// TODO weitere Modifikatoren mit einbeziehen

			if (characterId == voteOptionCharId)
				tmp = 125;

			options.put(vo.getValue(), tmp);
		}

		options = CollectionUtils.sortByValue(options);

		return ((Entry<Integer, Integer>) options.entrySet().toArray()[0])
				.getKey();

	}

}
