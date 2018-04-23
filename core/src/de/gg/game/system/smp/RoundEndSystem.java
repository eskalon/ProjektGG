package de.gg.game.system.smp;

import java.util.Map.Entry;

import de.gg.game.entity.Character;
import de.gg.game.entity.City;
import de.gg.game.entity.Player;
import de.gg.game.type.LawTypes;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.type.SocialStatusS;
import de.gg.game.type.SocialStatusS.SocialStatus;

public class RoundEndSystem {

	private City city;
	private short localPlayerId;

	/**
	 * @param localPlayerId
	 *            The id of the local player. <code>-1</code> if this system is
	 *            called by the server side simulation.
	 */
	public RoundEndSystem(short localPlayerId) {
		this.localPlayerId = localPlayerId;
	}

	public void init(City city) {
		this.city = city;
	}

	public void processCharacter(short id, Character c) {
		// AGE
		c.setAge(c.getAge() + 1);

		// AGE DAMAGE
		if (c.getAge() > 18) {
			if (c.getAge() > 45) {
				if (c.getAge() > 65) {
					// > 65
					c.setHp(c.getHp() - 3);
				} else {
					// 45-65
					c.setHp(c.getHp() - 2);
				}
			} else {
				// 19 - 45
				c.setHp(c.getHp() - 1);
			}
		}

		// SALARY
		PositionType position = c.getPosition();
		if (position != null) {
			c.setGold(c.getGold() + position.getSalary());
		}

		// TEMPORARY OPINION MODIFIERS
		for (Entry<Short, Integer> opinionEntry : c.getOpinionModifiers()
				.entrySet()) {
			if (opinionEntry.getValue() == -3 || opinionEntry.getValue() == 4) {
				c.getOpinionModifiers().remove(opinionEntry.getKey());
				break;
			}

			if (opinionEntry.getValue() > 0)
				opinionEntry.setValue(opinionEntry.getValue() - 4);
			if (opinionEntry.getValue() < 0)
				c.setReputationModifiers(c.getReputationModifiers() + 3);
		}

		// REPUTATION MODIFIERS
		if (c.getReputationModifiers() > 0)
			c.setReputationModifiers(c.getReputationModifiers() - 1);
		if (c.getReputationModifiers() < 0)
			c.setReputationModifiers(c.getReputationModifiers() + 1);
	}

	public void processPlayer(short id, Player p) {
		Character c = p.getCurrentlyPlayedCharacter();

		// INHERITANCE TAX
		if (p.getPreviouslyInheritedValue() > 0) {
			c.setGold(c.getGold() - Math.round(p.getPreviouslyInheritedValue()
					* ((Integer) city.getLaws().get(LawTypes.INHERITANCE_TAX)
							/ 100F)));
			p.setPreviouslyInheritedValue(0);
		}

		// SOCIAL STATUS
		switch (c.getStatus().getLevel()) {
		case 1: {
			SocialStatus superiorStatus = SocialStatusS
					.getByIndex(c.getStatus().getLevel() + 1);
			if (p.getFortune(city) >= superiorStatus.getFortuneRequirement()
					&& c.getHighestPositionLevel() >= superiorStatus
							.getPositionLevelRequirement()) {
				c.setStatus(superiorStatus);// PATRICIAN

				if (localPlayerId == id) {
					// TODO notification!
				}
			}
			break;
		}
		case 2: {
			SocialStatus superiorStatus = SocialStatusS
					.getByIndex(c.getStatus().getLevel() + 1);
			if (p.getFortune(city) >= superiorStatus.getFortuneRequirement()
					&& c.getHighestPositionLevel() >= superiorStatus
							.getPositionLevelRequirement()) {
				c.setStatus(superiorStatus);// CAVALIER

				if (localPlayerId == id) {
					// TODO notification!
				}
			}
			break;
		}
		case 3: {
			SocialStatus superiorStatus = SocialStatusS
					.getByIndex(c.getStatus().getLevel() + 1);
			if (p.getFortune(city) >= superiorStatus.getFortuneRequirement()
					&& c.getHighestPositionLevel() >= superiorStatus
							.getPositionLevelRequirement()) {
				c.setStatus(superiorStatus);// BARON

				if (localPlayerId == id) {
					// TODO notification!
				}
			}
			break;
		}
		}

		// AP
		p.setAvailableAp(
				p.getAvailableAp() + 4 + p.getSkills().getAgilitySkill());
	}

}
