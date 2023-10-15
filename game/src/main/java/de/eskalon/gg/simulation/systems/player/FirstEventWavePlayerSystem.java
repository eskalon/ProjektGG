package de.eskalon.gg.simulation.systems.player;

import java.util.Map.Entry;
import java.util.Random;

import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.utils.RandomUtils;
import de.eskalon.gg.graphics.ui.data.NotificationData;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.Character;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.model.entities.Position;
import de.eskalon.gg.simulation.model.types.PositionType;
import de.eskalon.gg.simulation.model.types.SocialStatus;
import de.eskalon.gg.simulation.systems.AbstractScheduledProcessingSystem;

public class FirstEventWavePlayerSystem
		extends AbstractScheduledProcessingSystem<Player> {

	private World world;
	private short localPlayerId;
	private Random random;

	public FirstEventWavePlayerSystem(World world, short localPlayerId) {
		super(100);
		this.world = world;
		this.localPlayerId = localPlayerId;
		this.random = new Random(world.getSeed());
	}

	@Override
	public void process(short id, Player p) {
		/* ILLNESS */
		if (p.isIll()) {
			if (RandomUtils.isTrue(random, 5)) { // Recuperation
				p.setIll(false);
			}
		} else { // Infection
			if (RandomUtils.isTrue(random, 90)) {
				p.setIll(true);
			}
		}

		/* AP */
		// if(p.getAvailableAp() > 17)
		// TODO remove some aps

		/* NOTIFICATIONS */
		if (id == localPlayerId) {
			Character c = p.getCurrentlyPlayedCharacter(world);

			/* OPEN POSITION */
			for (Entry<PositionType, Position> e : world.getPositions()
					.entrySet()) {
				if (!e.getValue().isHeld()) {
					if (e.getKey().getLevel() - 1 <= c
							.getHighestPositionLevel()) {
						if (e.getKey().getStatusRequirement() == null || e
								.getKey().getStatusRequirement()
								.getLevel() <= c.getStatus().getLevel()) {

							NotificationData not = new NotificationData(
									Lang.get(
											"notification.pos_available.title"),
									Lang.get("notification.pos_available.text",
											e.getKey()),
									null);

							// TODO create notification
							// eventBus.post(new
							// NotificationCreationEvent(not));
						}
					}
				}
			}

			/* BUY CITIZENSHIP */
			if (c.getStatus() == SocialStatus.NON_CITIZEN) {
				if (p.getFortune(world) >= SocialStatus.NON_CITIZEN
						.getFortuneRequirement()) {
					// TODO create notification
				}
			}
		}
	}

}
