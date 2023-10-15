package de.eskalon.gg.simulation.actions.handlers;

import de.eskalon.commons.net.packets.data.IPlayerAction;
import de.eskalon.gg.simulation.model.World;

public interface IPlayerActionHandler<A extends IPlayerAction> {

	public void handle(World world, short playerId, A action);

}
