package de.gg.game;

import de.gg.data.RoundEndData;

/**
 * Classes that implement this interface take care of the
 * {@linkplain SlaveActionHandler player actions} that actually get executed. In
 * sigleplayer games all player actions trigger methods of this interface, in
 * multiplayer sessions the client informs the server of their action and after
 * the server approves an action this interface takes care of executing it.
 */
public interface AuthoritativeResultListener {

	/**
	 * Called after a round ended to setup the next round.
	 * 
	 * @param data
	 *            This data contains all calculations done after a round i.e. a
	 *            salary costs, tuition effects, etc.
	 */
	public void onAllPlayersReadied(RoundEndData data);

}
