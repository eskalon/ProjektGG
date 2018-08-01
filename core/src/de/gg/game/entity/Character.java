package de.gg.game.entity;

import java.util.HashMap;

import de.gg.game.type.NPCCharacterTraits.CharacterTrait;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.type.Religion;
import de.gg.game.type.SocialStatusS.SocialStatus;

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
	 * <p>
	 * A positive value is denoting a loyal, trustful and law-abiding citizen.
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
	 * @return the character's reputation. Is never lower than <code>0</code>
	 *         and <i>usually</i> in the range of <code>0</code> and
	 *         <code>20</code>.
	 */
	public int getReputation() {
		int reputation = ((int) (highestPositionLevel * 1.5))
				+ (status.getLevel() * 3) + reputationModifiers;
		return reputation < 0 ? 0 : reputation;
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

	/**
	 * Adds an opinion another character has on this character. Takes the
	 * previously present modifiers into account.
	 * 
	 * @param charId
	 *            The other character's id.
	 * @param modifier
	 *            The opinion modifier.
	 * @see #opinionModifiers
	 */
	public void addOpinionModifier(short charId, int modifier) {
		Integer currentMod = opinionModifiers.get(charId);
		if (currentMod == null)
			currentMod = 0;

		// A character with a strong opinion isn't swayed easily in either a
		// positive nor negative direction
		if (Math.abs(currentMod) > 40)
			modifier = (int) Math.round(modifier * 0.7);
		else if (Math.abs(currentMod) > 20)
			modifier = (int) Math.round(modifier * 0.9);

		opinionModifiers.put(charId, currentMod + modifier);
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
