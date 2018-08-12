package de.gg.game.system.server;

import de.gg.game.entity.Player;
import de.gg.game.world.City;
import de.gg.network.rmi.AuthoritativeResultListener;

public class IllnessDamageSystem extends ServerProcessingSystem<Player> {

	private City city;

	public IllnessDamageSystem(AuthoritativeResultListener resultListener) {
		super(resultListener);
	}

	@Override
	public void init(City city, long seed) {
		this.city = city;
	}

	@Override
	public void process(short id, Player p) {
		if (p.isIll()) {
			p.getCurrentlyPlayedCharacter(city)
					.setHp(p.getCurrentlyPlayedCharacter(city).getHp() - 1);

			resultListener.onCharacterDamage(id, (short) 1);
		}
	}

	@Override
	public int getTickRate() {
		return 1800; // this system is called two times per round
	}

}
