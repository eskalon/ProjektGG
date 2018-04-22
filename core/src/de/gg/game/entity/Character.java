package de.gg.game.entity;

import java.util.HashMap;
import java.util.Random;

import de.gg.game.GameSession;
import de.gg.game.type.Religion;
import de.gg.game.type.NPCCharacterTraits.CharacterTrait;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.type.SocialStatusS.SocialStatus;
import de.gg.util.RandomUtils;

public class Character {

	private String name, surname;
	private boolean isMale;
	private Religion religion;

	private boolean isMarried;

	private int gold;

	private PositionType position;
	private SocialStatus status;
	private int highestPositionLevel;
	/**
	 * The reputation modifiers. Should be between <code>-20</code> and
	 * <code>+15</code>. Slowly shifts back to <code>0</code>.
	 */
	private int reputationModifiers;
	/**
	 * The health points of the character.
	 */
	private int hp = 100;
	private int age;
	/**
	 * Contains all temporary opinion modifiers other characters have of this
	 * character. I.e. if this character does something good/bad for someone
	 * else that changes their opinion, the respective opinion modifier is saved
	 * in this list.
	 * <p>
	 * Should be <i>at max</i> <code>+/-50</code> .
	 */
	private HashMap<Short, Integer> opinionModifiers = new HashMap<>();
	/**
	 * A trait denoting the npc's behavior in certain situations. Only set for
	 * non-player characters.
	 */
	private CharacterTrait trait;

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
		Character thisCharacter = session.getCity().getCharacters()
				.get(thisCharacterId);
		Character otherCharacter = session.getCity().getCharacters()
				.get(otherCharacterId);

		int opinion = 0;

		// Difficulty (-4, 10)
		opinion += session.getDifficulty().getOpinionModifer();

		// Base Opinion (14, 43)
		Random r = new Random(thisCharacterId * otherCharacterId);
		opinion += RandomUtils.getRandomNumber(r, -9, 20) + 23;

		// NPC Opinion Modifier (0, 10)
		opinion += otherCharacter.getNPCTrait().getBaseOpinionModifier();

		// Reputation (0, 20)
		opinion += thisCharacter.getReputation();

		// TODO Kinship
		// +20 für Kinder und Eltern

		// Religion (5, 12)
		boolean isReligionImportant = otherCharacter.getNPCTrait()
				.isReligionImportant();
		if (thisCharacter.getReligion() == otherCharacter.getReligion())
			opinion += isReligionImportant ? 16 : 11;
		else
			opinion += isReligionImportant ? 0 : 5;

		// Temporary Opinion Modifiers
		if (thisCharacter.getOpinionModifiers().containsKey(otherCharacterId))
			opinion += Math.round(
					thisCharacter.getOpinionModifiers().get(otherCharacterId));

		// Temporary Round Modifier (-3, 4)
		opinion += getPerRoundAndCharacterPopularityModifier(
				session.getGameSeed(), thisCharacterId, otherCharacterId,
				session.getRound());

		return opinion < 0 ? 0 : opinion;
	}

	/**
	 * @return the character's reputation. Is never lower than <code>0</code>
	 *         and <i>usually</i> in the range of <code>0</code> and
	 *         <code>20</code>.
	 */
	public int getReputation() {
		int reputation = ((int) (highestPositionLevel * 1.5))
				+ (status.getLevel() * 3) + reputationModifiers;
		return reputation < 0 ? 0 : reputation;
	}

	/**
	 * Returns the popularity modifier of a character towards another character
	 * in this specific round.
	 * 
	 * @param gameSeed
	 *            The seed of this game session.
	 * @param characterIdA
	 *            The id of character a.
	 * @param characterIdB
	 *            The id of character b.
	 * @param round
	 *            The current round.
	 * @return The popularity modifier of character a towards character b in
	 *         this specific round.
	 */
	public static int getPerRoundAndCharacterPopularityModifier(long gameSeed,
			short characterIdA, short characterIdB, int round) {
		Random r = new Random(gameSeed * characterIdA * characterIdB * round);
		return RandomUtils.getRandomNumber(r, -3, 4);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public boolean isMale() {
		return isMale;
	}

	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	public Religion getReligion() {
		return religion;
	}

	public void setReligion(Religion religion) {
		this.religion = religion;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public PositionType getPosition() {
		return position;
	}

	public void setPosition(PositionType position) {
		this.position = position;
	}

	public SocialStatus getStatus() {
		return status;
	}

	public void setStatus(SocialStatus status) {
		this.status = status;
	}

	public int getReputationModifiers() {
		return reputationModifiers;
	}

	public void setReputationModifiers(int reputationModifiers) {
		this.reputationModifiers = reputationModifiers;
	}

	/**
	 * @return the character's health. Is 100 at the birth.
	 */
	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public HashMap<Short, Integer> getOpinionModifiers() {
		return opinionModifiers;
	}

	public CharacterTrait getNPCTrait() {
		return trait;
	}

	public void setNPCTrait(CharacterTrait trait) {
		this.trait = trait;
	}

	public int getHighestPositionLevel() {
		return highestPositionLevel;
	}

	public void setHighestPositionLevel(int highestPositionLevel) {
		this.highestPositionLevel = highestPositionLevel;
	}

	/**
	 * @return whether this character is married to a player.
	 */
	public boolean isMarried() {
		return isMarried;
	}

	public void setMarried(boolean isMarried) {
		this.isMarried = isMarried;
	}

}
