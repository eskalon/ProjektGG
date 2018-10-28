package de.gg.game.systems.server;

import de.gg.game.entities.Player;
import de.gg.game.network.rmi.AuthoritativeResultListener;
import de.gg.game.world.World;

public class IllnessDamageSystem extends ServerProcessingSystem<Player> {

	private World world;

	public IllnessDamageSystem(AuthoritativeResultListener resultListener) {
		super(resultListener, 1800, true); // this system is called two times
											// per round
	}

	@Override
	public void init(World world, long seed) {
		this.world = world;
	}

	@Override
	public void process(short id, Player p) {
		if (p.isIll()) {
			p.getCurrentlyPlayedCharacter(world)
					.setHp(p.getCurrentlyPlayedCharacter(world).getHp() - 1);

			resultListener.onCharacterDamage(id, (short) 1);
		}
	}
}
