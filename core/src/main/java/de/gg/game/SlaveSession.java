package de.gg.game;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.google.common.eventbus.EventBus;

import de.gg.events.AllPlayersReadyEvent;
import de.gg.events.ChangedGameSpeedEvent;
import de.gg.events.NewVoteEvent;
import de.gg.events.ServerReadyEvent;
import de.gg.events.VoteFinishedEvent;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.entities.Player;
import de.gg.game.systems.ProcessingSystem;
import de.gg.game.systems.client.FirstEventWaveClientSystem;
import de.gg.game.types.GameSpeed;
import de.gg.game.types.PositionType;
import de.gg.game.votes.ImpeachmentVote;
import de.gg.game.votes.VoteableMatter;
import de.gg.network.LobbyPlayer;
import de.gg.network.rmi.AuthoritativeResultListener;
import de.gg.utils.Log;

/**
 * This class simulates a game session on the client of a multiplayer game. It
 * is also implementing the interface used for the RMI by the server.
 */
public class SlaveSession extends GameSession
		implements AuthoritativeResultListener {

	private EventBus eventBus;

	private GameClock clock;

	/**
	 * Creates a new multiplayer session.
	 *
	 * @param eventBus
	 *            the game's event bus.
	 * @param sessionSetup
	 *            the settings of the game session.
	 * @param players
	 *            a hashmap containing the players.
	 * @param networkID
	 *            the networkID of the local player.
	 */
	public SlaveSession(EventBus eventBus, GameSessionSetup sessionSetup,
			HashMap<Short, LobbyPlayer> players, short networkID) {
		super(sessionSetup, players, networkID);
		this.clock = new GameClock(eventBus);
		this.eventBus = eventBus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(@Nullable SavedGame savedGame) {
		super.init(savedGame);

		// Setup the client systems
		ProcessingSystem<Player> s;
		s = new FirstEventWaveClientSystem(eventBus, localNetworkId);
		s.init(city, getGameSeed());
		this.playerSystems.add(s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void fixedUpdate() {
		super.fixedUpdate();

		if (isRightTick(TICKS_PER_SECOND)) {
			clock.update();
		}

		if (isRightTick(15)) {
			Log.debug("CLOCK", "%02d:%02d", getClock().getHour(),
					getClock().getMinute());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void startNextRound() {
		super.startNextRound();

		clock.resetClock();
	}

	@Override
	public synchronized void onAllPlayersReadied() {
		Log.debug("Client", "Alle Spieler sind bereit! Nächste Runde startet");

		this.startNextRound();
		eventBus.post(new AllPlayersReadyEvent(true));
	}

	@Override
	public void onServerReady() {
		Log.info("Client", "Der Server is bereit");
		Log.debug("Client", "%d Ticks hinter der Server-Simulation",
				TICKS_PER_ROUND - getTickCount());
		eventBus.post(new ServerReadyEvent());
	}

	@Override
	protected void onNewVote(VoteableMatter matterToVoteOn) {
		eventBus.post(new NewVoteEvent(matterToVoteOn));
	}

	@Override
	public void onVoteFinished(VoteResults results) {
		eventBus.post(new VoteFinishedEvent(results, matterToVoteOn));

		finishCurrentVote(results);
	}

	@Override
	public void onCharacterDeath(short characterId) {
		// TODO Todeseffekte (siehe
		// FirstCharacterEventWaveServerSystem#process())

		// außerdem wenn lokaler Spieler oder Verwandter dann
		// Notification-Banner (-> Banner-Event das UI Reloaded für
		// MapScreen; Notification-List in GameSession)
	}

	@Override
	public void onCharacterDamage(short characterId, short damage) {
		city.getCharacters().get(characterId)
				.setHp(city.getCharacters().get(characterId).getHp() - damage);

	}

	@Override
	public void onPlayerIllnessChange(short playerId, boolean isIll) {
		city.getPlayers().get(playerId).setIll(isIll);
	}

	@Override
	public void setGameSpeed(int index) {
		setGameSpeed(GameSpeed.values()[index]);

		eventBus.post(new ChangedGameSpeedEvent(gameSpeed));
	}

	/**
	 * @return the clock used to determine the current in-game time.
	 */
	public GameClock getClock() {
		return clock;
	}

	@Override
	public void onAppliedForPosition(short characterId, PositionType type) {
		city.getPositions().get(type).getApplicants().add(characterId);
	}

	@Override
	public void onImpeachmentVoteArranged(short targetCharacterId,
			short callerCharacterId) {
		city.getMattersToHoldVoteOn()
				.add(new ImpeachmentVote(city, city.getCharacters()
						.get(targetCharacterId).getPosition(),
						callerCharacterId));
	}

}
