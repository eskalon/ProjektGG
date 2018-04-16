package de.gg.entity;

import java.util.HashMap;
import java.util.Random;

import de.gg.entity.NPCCharacterTraits.CharacterTrait;
import de.gg.entity.PositionTypes.PositionType;
import de.gg.entity.SocialStatusS.SocialStatus;
import de.gg.util.RandomUtils;

public class Character {

	private String name, surname;
	private boolean isMale;
	private Religion religion;

	private int gold;
	private PositionType position;
	private SocialStatus status;
	private int highestPositionLevel;
	private int reputationModifiers;
	private int hp;
	private int age;
	private HashMap<Character, Integer> popularityModifiers;
	/**
	 * A trait denoting the npc's behavior in certain situations. Only set for
	 * non-player characters.
	 */
	private CharacterTrait trait;

	public int getReputation() {
		return highestPositionLevel + reputationModifiers;
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
	public int getPerRoundAndCharacterPopularityModifier(long gameSeed,
			int characterIdA, int characterIdB, int round) {
		Random r = new Random(gameSeed * characterIdA * characterIdB * round);
		return RandomUtils.getRandomNumber(r, -5, 6);
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

	public void setHighestPositionLevel(int highestPositionLevel) {
		this.highestPositionLevel = highestPositionLevel;
	}

	public int getReputationModifiers() {
		return reputationModifiers;
	}

	public void setReputationModifiers(int reputationModifiers) {
		this.reputationModifiers = reputationModifiers;
	}

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

	public HashMap<Character, Integer> getPopularityModifiers() {
		return popularityModifiers;
	}

	public CharacterTrait getNPCTrait() {
		return trait;
	}

	public void setNPCTrait(CharacterTrait trait) {
		this.trait = trait;
	}

	public enum Religion {
		CATHOLIC, ORTHODOX;
	}

}
