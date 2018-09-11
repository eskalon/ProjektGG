package de.gg.game.systems.server;

import de.gg.game.entities.Player;
import de.gg.game.world.World;
import de.gg.network.rmi.AuthoritativeResultListener;

public class IllnessDamageSystem extends ServerProcessingSystem<Player> {

	private World world;

	public IllnessDamageSystem(AuthoritativeResultListener resultListener) {
		super(resultListener);
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

	@Override
	public int getTickRate() {
		return 1800; // this system is called two times per round
	}

}
