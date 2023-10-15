package de.eskalon.gg.simulation.actions.handlers;

import de.eskalon.commons.net.SimpleGameServer;
import de.eskalon.gg.simulation.actions.GameSpeedChangeAction;
import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.types.GameSpeed;

public class GameSpeedChangeActionHandler
		implements IPlayerActionHandler<GameSpeedChangeAction> {

	@Override
	public void handle(World world, short playerId,
			GameSpeedChangeAction action) {
		//TODO
		
		if (!server.getServerSetup().isHostOnlyCommands()
				|| clientId == SimpleGameServer.HOST_PLAYER_NETWORK_ID) {
			
			
			int index = session.getGameSpeed().ordinal() + 1; // or -1

			session.setGameSpeed(GameSpeed
					.values()[index >= GameSpeed.values().length ? 0 : index]);
		}
	}

}
