package de.gg.game.network.rmi;

import java.util.HashMap;

import javax.annotation.Nullable;

import de.gg.game.model.types.PositionType;
import de.gg.game.model.votes.BallotResults;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.session.SavedGame;

/**
 * Classes that implement this interface take care of the
 * {@linkplain SlaveActionListener player actions} that actually get executed.
 * The client informs the server of their action via a
 * {@linkplain SlaveActionListener} and after the server approves an action this
 * interface takes care of executing it.
 */
public interface AuthoritativeResultListener {

	/* --- BASIC LOBBY STUFF --- */
	/**
	 * This method is the called by the server after the client
	 * {@linkplain SlaveActionListener#requestGameData(short) requested the game
	 * data} and contains all the necessary information about the match.
	 *
	 * @param players
	 *            A hashmap of all players keyed by their respective IDs.
	 * @param sessionSetup
	 *            The game session's setup.
	 * @param savedGame
	 *            The loaded game session. <code>Null</code> if this match isn't
	 *            loaded.
	 */
	public void onGameSetup(HashMap<Short, LobbyPlayer> players,
			GameSessionSetup sessionSetup, @Nullable SavedGame savedGame);

	public void onPlayerLeft(short senderId);

	public void onPlayerJoined(short senderId, LobbyPlayer lobbyPlayer);

	public void onLobbyPlayerChanged(short senderId, LobbyPlayer lobbyPlayer);

	public void onChatMessage(short senderId, String message);

	/* --- ROUND SETUP --- */
	/**
	 * Called after all players readied up.
	 */
	public void onAllPlayersReadied();

	/**
	 * Called when the server is ready to continue the game session's
	 * processing. Is normally called when a round is over.
	 */
	public void onServerReady();

	/* --- ENTITY SYNC --- */
	public void onCharacterDeath(short characterId);

	public void onCharacterDamage(short characterId, short damage);

	public void onPlayerIllnessChange(short playerId, boolean isIll);

	/* --- VOTES --- */
	/**
	 * Called when a certain vote is finished i.e. every member cast his vote.
	 *
	 * @param voteResult
	 *            The vote result.
	 */
	public void onVoteFinished(BallotResults voteResult);

	public void onAppliedForPosition(short clientId, PositionType type);

	public void onImpeachmentVoteArranged(short targetCharacterId,
			short callerCharacterId);

	/* --- MISC PLAYER ACTIONS --- */
	public void setGameSpeed(int index);

}
