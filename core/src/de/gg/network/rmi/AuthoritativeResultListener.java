package de.gg.network.rmi;

import de.gg.game.data.RoundEndData;

/**
 * Classes that implement this interface take care of the
 * {@linkplain SlaveActionListener player actions} that actually get executed.
 * The client informs the server of their action via the
 * {@linkplain SlaveActionListener} and after the server approves an action this
 * interface takes care of executing it.
 */
public interface AuthoritativeResultListener {

	/**
	 * Called after all players readied up.
	 */
	public void onAllPlayersReadied();

	/**
	 * Called after a round ended to inform about the changes.
	 * 
	 * @param data
	 *            this data contains all calculations done after a round i.e. a
	 *            salary costs, tuition effects, etc.
	 */
	public void onRoundEnd(RoundEndData data);

	public void onCharacterDeath(short characterId);

	public void onCharacterDamage(short characterId, short damage);

	public void onPlayerIllnessChange(short playerId, boolean isIll);

	public void setGameSpeed(int index);

}
