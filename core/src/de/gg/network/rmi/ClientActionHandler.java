package de.gg.network.rmi;

import de.gg.game.type.PositionTypes.PositionType;

/**
 * This class is an convenience wrapper for {@link SlaveActionListener}. It is
 * used to relay the player actions to the server.
 */
public class ClientActionHandler {

	private short networkId;
	private SlaveActionListener actionListener;

	public ClientActionHandler(short networkId,
			SlaveActionListener actionListener) {
		this.networkId = networkId;
		this.actionListener = actionListener;
	}

	public boolean readyUp() {
		return actionListener.readyUp(networkId);
	}

	public void increaseGameSpeed() {
		actionListener.increaseGameSpeed(networkId);
	}

	public void decreaseGameSpeed() {
		actionListener.decreaseGameSpeed(networkId);
	}

	public void castVote(int vote) {
		actionListener.onVoteCast(vote, networkId);
	}

	public void applyForPosition(PositionType t) {
		actionListener.onAppliedForPosition(t, networkId);
	}

	public void arrangeImpeachmentVote(short targetCharacterId) {
		actionListener.onImpeachmentVoteArranged(targetCharacterId, networkId);
	}

}
