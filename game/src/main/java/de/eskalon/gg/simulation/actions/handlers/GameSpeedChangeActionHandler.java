package de.eskalon.gg.simulation.actions.handlers;

import com.badlogic.gdx.math.MathUtils;

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

		int newIndex = MathUtils.clamp(
				world.getGameSpeed().ordinal() + (action.isSpeedUp() ? 1 : -1),
				0, GameSpeed.values().length - 1);
		world.setGameSpeed(GameSpeed.values()[newIndex]);
	}

}
