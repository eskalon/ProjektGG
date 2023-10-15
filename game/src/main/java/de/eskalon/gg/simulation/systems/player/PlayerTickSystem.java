package de.eskalon.gg.simulation.systems.player;

import de.eskalon.gg.simulation.model.World;
import de.eskalon.gg.simulation.model.entities.Player;
import de.eskalon.gg.simulation.systems.AbstractContinuousProcessingSystem;

public class PlayerTickSystem
		extends AbstractContinuousProcessingSystem<Player> {

	private World world;

	public PlayerTickSystem(World world) {
		super(100); // this system is called three times per round
		this.world = world;
	}

	@Override
	public void process(short id, Player p) {
		if (p.isIll()) {
			p.getCurrentlyPlayedCharacter(world)
					.setHp(p.getCurrentlyPlayedCharacter(world).getHp() - 1);
		}
	}

}
