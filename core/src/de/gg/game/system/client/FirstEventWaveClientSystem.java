package de.gg.game.system.client;

import java.util.Map.Entry;

import com.google.common.eventbus.EventBus;

import de.gg.event.NewNotificationEvent;
import de.gg.game.data.NotificationData;
import de.gg.game.entity.Character;
import de.gg.game.entity.Player;
import de.gg.game.entity.Position;
import de.gg.game.system.ProcessingSystem;
import de.gg.game.type.PositionTypes.PositionType;
import de.gg.game.type.SocialStatusS;
import de.gg.game.world.City;

/**
 * This system processes after 60 seconds and takes care of the first wave of
 * events on the client side.
 */
public class FirstEventWaveClientSystem extends ProcessingSystem<Player> {

	private City city;
	private EventBus eventBus;
	private short localPlayerId;

	public FirstEventWaveClientSystem(EventBus eventBus, short localPlayerId) {
		this.eventBus = eventBus;
		this.localPlayerId = localPlayerId;
	}

	@Override
	public void init(City city, long seed) {
		this.city = city;
	}

	@Override
	public void process(short id, Player p) {
		if (id == localPlayerId) {
			Character c = p.getCurrentlyPlayedCharacter(city);

			// Inform about open positions
			for (Entry<PositionType, Position> e : city.getPositions()
					.entrySet()) {
				if (!e.getValue().isHeld()) {
					if (e.getKey().getLevel() - 1 <= c
							.getHighestPositionLevel()) {
						if (e.getKey().getStatusRequirement() == null || e
								.getKey().getStatusRequirement()
								.getLevel() <= c.getStatus().getLevel()) {

							NotificationData not = new NotificationData(
									"Amt verfügbar",
									String.format(
											"In dieser Runde könnt ihr euch auf das Amt des %ss bewerben",
											e.getKey().getName()),
									null);

							eventBus.post(new NewNotificationEvent(not));
						}
					}
				}
			}

			if (c.getStatus() == SocialStatusS.NON_CITIZEN) {
				// TODO inform about possibility to buy citizen status
			}
		}
	}

	@Override
	public boolean isProcessedContinuously() {
		return false;
	}

	@Override
	public int getTickRate() {
		return 600;
	}

}
