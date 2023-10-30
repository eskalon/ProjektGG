package de.eskalon.gg.simulation.systems.player;

import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.types.LawType;
import de.eskalon.gg.simulation.model.types.SocialStatus;
import de.eskalon.gg.simulation.systems.AbstractScheduledProcessingSystem;

public class RoundStartPlayerSystem
		extends AbstractScheduledProcessingSystem<Player> {

	private World world;
	private short localPlayerId;

	public RoundStartPlayerSystem(World world, short localPlayerId) {
		super(0);
		this.world = world;
		this.localPlayerId = localPlayerId;
	}

	@Override
	public void process(short id, Player p) {
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
				// c.setStatus(superiorStatus);

				if (localPlayerId == id) {
					// TODO notification!
				}
			}
		}

		// AP
		p.setAvailableAP(
				p.getAvailableAP() + 4 + p.getSkills().getAgilitySkill());
	}

}
