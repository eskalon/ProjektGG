package de.gg.game.system.server;

import de.gg.game.AuthoritativeSession;
import de.gg.game.entity.Player;

public class IllnessDamageSystem extends ServerProcessingSystem<Player> {

	public IllnessDamageSystem(AuthoritativeSession serverSession) {
		super(serverSession);
	}

	@Override
	public void process(short id, Player p) {
		if (p.isIll()) {
			p.getCurrentlyPlayedCharacter()
					.setHp(p.getCurrentlyPlayedCharacter().getHp() - 1);

			serverSession.getResultListenerStub().onCharacterDamage(id,
					(short) 1);
		}
	}

	@Override
	public int getTickRate() {
		return 1800; // this system is called two times per round
	}

}
