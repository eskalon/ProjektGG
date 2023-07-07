package de.gg.game.systems.smp;

import java.util.Map.Entry;

import de.gg.game.model.World;
import de.gg.game.model.entities.Character;
import de.gg.game.model.entities.Player;
import de.gg.game.model.entities.Position;
import de.gg.game.model.types.LawType;
import de.gg.game.model.types.PositionType;
import de.gg.game.model.types.SocialStatus;
import de.gg.game.model.votes.ElectionBallot;

public class RoundEndSystem {

	private World world;
	private short localPlayerId;

	/**
	 * @param localPlayerId
	 *            The id of the local player. <code>-1</code> if this system is
	 *            called by the server side simulation.
	 */
	public RoundEndSystem(short localPlayerId) {
		this.localPlayerId = localPlayerId;
	}

	public void init(World world) {
		this.world = world;
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
		// TODO statt am Rundenende am Rundenstart berechnen
		PositionType position = c.getPosition();
		if (position != null) {
			c.setGold(c.getGold() + position.getSalary());
		}

		// TEMPORARY OPINION MODIFIERS
		for (Entry<Short, Integer> opinionEntry : c.getOpinionModifiers()
				.entrySet()) {
			if (opinionEntry.getValue() > 0) {
				opinionEntry.setValue(opinionEntry.getValue() - 4);
				if (opinionEntry.getValue() < 0)
					c.getOpinionModifiers().remove(opinionEntry.getKey());
			}
			if (opinionEntry.getValue() < 0) {
				opinionEntry.setValue(opinionEntry.getValue() + 3);
				if (opinionEntry.getValue() > 0)
					c.getOpinionModifiers().remove(opinionEntry.getKey());
			}
		}

		// REPUTATION MODIFIERS
		if (c.getReputationModifiers() > 0)
			c.setReputationModifiers(c.getReputationModifiers() - 1);
		if (c.getReputationModifiers() < 0)
			c.setReputationModifiers(c.getReputationModifiers() + 1);
	}

	public void processPlayer(short id, Player p) {
		Character c = p.getCurrentlyPlayedCharacter(world);

		// INHERITANCE TAX
		if (p.getPreviouslyInheritedValue() > 0) {
			c.setGold(c.getGold() - Math.round(p.getPreviouslyInheritedValue()
					* ((Integer) world.getLaws().get(LawType.INHERITANCE_TAX)
							/ 100F)));
			p.setPreviouslyInheritedValue(0);
		}

		// SOCIAL STATUS
		if (c.getStatus().getLevel() < 3) {
			// Patrician & Cavalier
			SocialStatus superiorStatus = SocialStatus
					.values()[c.getStatus().getLevel() + 1];
			if (p.getFortune(world) >= superiorStatus.getFortuneRequirement()
					&& c.getHighestPositionLevel() >= superiorStatus
							.getPositionLevelRequirement()) {
				c.setStatus(superiorStatus);//

				if (localPlayerId == id) {
					// TODO notification!
				}
			}
		}

		// AP
		p.setAvailableAP(
				p.getAvailableAP() + 4 + p.getSkills().getAgilitySkill());
	}

	public void processPosition(PositionType type, Position p) {
		// Add the election to the matters to vote on
		if (p.hasApplicants())
			world.getMattersToHoldVoteOn().add(new ElectionBallot(world, type));
	}

}
