package de.eskalon.gg.net;

import de.damios.guacamole.concurrent.ThreadHandler;
import de.eskalon.commons.net.SimpleGameServer;
import de.eskalon.gg.misc.CollectionUtils;
import de.eskalon.gg.net_old.AuthoritativeSession;
import de.eskalon.gg.net_old.ChangedGameSpeedEvent;
import de.eskalon.gg.net_old.SlaveSession;
import de.eskalon.gg.simulation.model.entities.Position;
import de.eskalon.gg.simulation.model.types.GameSpeed;
import de.eskalon.gg.simulation.model.types.PositionType;
import de.eskalon.gg.simulation.model.votes.ElectionBallot;
import de.eskalon.gg.simulation.model.votes.ImpeachmentBallot;

public class XYZZZ {

	public void onImpeachmentVoteArranged(short targetCharacterId,
			short callerCharacterId) {
		world.getMattersToHoldVoteOn()
				.add(new ImpeachmentBallot(world, world.getCharacters()
						.get(targetCharacterId).getPosition(),
						callerCharacterId));
	}
	
	@Override
	public boolean onImpeachmentVoteArranged(short targetCharacterId,
			short clientId) {
		PositionType t = world.getCharacters().get(targetCharacterId)
				.getPosition();

		if (t != null) {
			// TODO überprüfen, ob nicht bereits ein anderer einen Vote
			// initiiert hat

			world.getMattersToHoldVoteOn().add(new ImpeachmentBallot(world, t,
					world.getPlayer(clientId).getCurrentlyPlayedCharacterId()));

			clientResultListeners.onImpeachmentVoteArranged(targetCharacterId,
					world.getPlayer(clientId).getCurrentlyPlayedCharacterId());
			return true;
		}

		return false;
	}

	@Override
	public boolean onAppliedForPosition(PositionType t, short clientId) {
		Position pos = world.getPositions().get(t);

		if (pos.getCurrentHolder() == (short) -1
				&& pos.getApplicants().size() < 4) {
			pos.getApplicants().add(
					world.getPlayer(clientId).getCurrentlyPlayedCharacterId());

			clientResultListeners.onAppliedForPosition(clientId, t);

			return true;
		}

		return false;
	}
	
	@Override
	public void onRoundEnd(short id, Position p) {
		if (p.hasApplicants())
			handler.getMattersToHoldVoteOn().add(new ElectionBallot(world,
					CollectionUtils.getKeyByValue(world.getPositions(), p)));
	}

}
