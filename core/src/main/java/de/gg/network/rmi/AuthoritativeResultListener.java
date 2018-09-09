package de.gg.network.rmi;

import de.gg.game.data.vote.VoteResults;
import de.gg.game.types.PositionType;

/**
 * Classes that implement this interface take care of the
 * {@linkplain SlaveActionListener player actions} that actually get executed.
 * The client informs the server of their action via the
 * {@linkplain SlaveActionListener} and after the server approves an action this
 * interface takes care of executing it.
 */
public interface AuthoritativeResultListener {

	// ROUND SETUP
	/**
	 * Called after all players readied up.
	 */
	public void onAllPlayersReadied();

	/**
	 * Called when the server is ready to continue the game session's
	 * processing. Is normally called when a round is over.
	 */
	public void onServerReady();

	// ENTITY SYNC
	public void onCharacterDeath(short characterId);

	public void onCharacterDamage(short characterId, short damage);

	public void onPlayerIllnessChange(short playerId, boolean isIll);

	// VOTES
	/**
	 * Called when a certain vote is finished i.e. every member cast his vote.
	 * 
	 * @param results
	 *            The results of the vote.
	 */
	public void onVoteFinished(VoteResults results);

	public void onAppliedForPosition(short clientId, PositionType type);

	public void onImpeachmentVoteArranged(short targetCharacterId,
			short callerCharacterId);

	// MISC PLAYER ACTIONS
	public void setGameSpeed(int index);

}
