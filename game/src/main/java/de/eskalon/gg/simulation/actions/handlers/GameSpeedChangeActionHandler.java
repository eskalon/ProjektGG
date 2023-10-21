package de.eskalon.gg.simulation.actions.handlers;

import de.eskalon.gg.simulation.actions.GameSpeedChangeAction;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.types.GameSpeed;

public class GameSpeedChangeActionHandler
		implements IPlayerActionHandler<GameSpeedChangeAction> {

	@Override
	public void handle(World world, short playerId,
			GameSpeedChangeAction action) {
		// TODO check permissions: !server.getServerSetup().isHostOnlyCommands()
		// || clientId == SimpleGameServer.HOST_PLAYER_NETWORK_ID

		int newIndex = world.getGameSpeed().ordinal()
				+ (action.isSpeedUp() ? 1 : -1);
		world.setGameSpeed(
				GameSpeed.values()[newIndex % GameSpeed.values().length]);
	}

}
