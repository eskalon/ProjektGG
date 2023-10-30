package de.eskalon.gg.simulation.model.entities;

import java.util.HashMap;

import de.eskalon.commons.lang.ILocalized;
import de.eskalon.gg.simulation.model.types.NPCCharacterTrait;
import de.eskalon.gg.simulation.model.types.PositionType;
import de.eskalon.gg.simulation.model.types.Religion;
import de.eskalon.gg.simulation.model.types.SocialStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public final class Character implements ILocalized {

	private @Getter @Setter String name, surname;
	private @Getter @Setter boolean isMale;
	private @Getter @Setter Religion religion;

	/**
	 * Whether this character is married to a player.
	 */
	private @Getter @Setter boolean isMarried;

	private @Getter @Setter int gold;

	private @Getter @Setter PositionType position;
	private @Getter @Setter SocialStatus status;
	private @Getter @Setter int highestPositionLevel;
	/**
	 * The reputation modifiers. Should be between {@code -20} and {@code 15}
	 * Slowly shifts back to {@code 0}
	 * <p>
	 * A positive value is denoting a loyal, trustful and law-abiding citizen.
	 */
	private @Getter @Setter int reputationModifiers;
	/**
	 * The health points of the character. Is {@code 100} at the birth.
	 */
	private @Getter @Setter int hp = 100;
	private @Getter @Setter int age;
	/**
	 * Contains all temporary opinion modifiers other characters have of this
	 * character. I.e. if this character does something good/bad for someone
	 * else that changes their opinion, the respective opinion modifier is saved
	 * in this list.
	 * <p>
	 * Should be <i>at max</i> <code>+/-50</code> .
	 */
	private @Getter HashMap<Short, Integer> opinionModifiers = new HashMap<>();
	/**
	 * A trait denoting the npc's behavior in certain situations. Only set for
	 * non-player characters.
	 */
	private @Getter @Setter NPCCharacterTrait npcTrait;

	/**
	 * @return the character's reputation. Is never lower than {@code 0} and
	 *         <i>usually</i> in the range of {@code 0} and {@code 20}.
	 */
	public int getReputation() {
		int reputation = ((int) (highestPositionLevel * 1.5))
				+ (status.getLevel() * 3) + reputationModifiers;
		return reputation < 0 ? 0 : reputation;
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
		if (Math.abs(currentMod) > 50)
			modifier = (int) Math.round(modifier * 0.6);
		else if (Math.abs(currentMod) > 40)
			modifier = (int) Math.round(modifier * 0.7);
		else if (Math.abs(currentMod) > 20)
			modifier = (int) Math.round(modifier * 0.9);

		opinionModifiers.put(charId, currentMod + modifier);
	}

	@Override
	public String getLocalizedName() {
		return name + " " + surname;
	}

}
