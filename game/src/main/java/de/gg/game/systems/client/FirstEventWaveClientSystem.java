package de.gg.game.systems.client;

import java.util.Map.Entry;

import com.google.common.eventbus.EventBus;

import de.eskalon.commons.lang.Lang;
import de.gg.game.events.NotificationCreationEvent;
import de.gg.game.model.World;
import de.gg.game.model.entities.Character;
import de.gg.game.model.entities.Player;
import de.gg.game.model.entities.Position;
import de.gg.game.model.types.PositionType;
import de.gg.game.model.types.SocialStatus;
import de.gg.game.systems.ProcessingSystem;
import de.gg.game.ui.data.NotificationData;

/**
 * This system processes after 60 seconds and takes care of the first wave of
 * events on the client side.
 */
public class FirstEventWaveClientSystem extends ProcessingSystem<Player> {

	private World world;
	private EventBus eventBus;
	private short localPlayerId;

	public FirstEventWaveClientSystem(EventBus eventBus, short localPlayerId) {
		super(600, false);
		this.eventBus = eventBus;
		this.localPlayerId = localPlayerId;
	}

	@Override
	public void init(World world, long seed) {
		this.world = world;
	}

	@Override
	public void process(short id, Player p) {
		if (id == localPlayerId) {
			Character c = p.getCurrentlyPlayedCharacter(world);

			// Inform about open positions
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

							eventBus.post(new NotificationCreationEvent(not));
						}
					}
				}
			}

			if (c.getStatus() == SocialStatus.NON_CITIZEN) {
				if (p.getFortune(world) >= SocialStatus.NON_CITIZEN
						.getFortuneRequirement()) {
					// TODO inform about possibility to buy citizen status
				}
			}
		}
	}

}
