package de.gg.network.rmi;

import de.gg.game.types.PositionType;

/**
 * This interface takes care of a player's actions. Those are mainly triggered
 * via the UI and then relayed to the server via this interface. The
 * implementing server-side class is responsible for informing the server of the
 * planned action. The server returns a boolean, <code>true</code> indicating a
 * successful action (-> the UI effect gets triggered, but the actual game does
 * <i>not</i> change), <code>false</code> indicating the need to reload the UI
 * (because a value seems to be out of synch and should be synced by now).
 * Actual changes to the game do not happen when a method of this class is
 * called, but wait for the server's call to the local
 * {@link AuthoritativeResultListener}.
 */
public interface SlaveActionListener {

	public boolean readyUp(short networkId);

	public void increaseGameSpeed(short clientId);

	public void decreaseGameSpeed(short clientId);

	public void onVoteCast(int vote, short clientId);

	public boolean onAppliedForPosition(PositionType t, short clientId);

	public boolean onImpeachmentVoteArranged(short targetCharacterId,
			short clientId);

}
