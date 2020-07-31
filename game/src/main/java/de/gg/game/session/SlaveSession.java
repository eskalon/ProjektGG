package de.gg.game.session;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.google.common.eventbus.EventBus;

import de.damios.guacamole.gdx.Log;
import de.gg.game.events.NewBallotEvent;
import de.gg.game.misc.GameClock;
import de.gg.game.model.entities.Player;
import de.gg.game.model.votes.Ballot;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.systems.ProcessingSystem;
import de.gg.game.systems.client.FirstEventWaveClientSystem;

/**
 * This class simulates a game session on the client of a multiplayer game.
 */
public class SlaveSession extends GameSession {

	private EventBus eventBus;
	private GameClock clock;

	/**
	 * Creates a new multiplayer session.
	 *
	 * @param eventBus
	 *            the game's event bus.
	 * @param sessionSetup
	 *            the settings of the game session.
	 * @param networkID
	 *            the network ID of the local player.
	 */
	public SlaveSession(EventBus eventBus, GameSessionSetup sessionSetup,
			short networkID) {
		super(sessionSetup, networkID);
		this.clock = new GameClock(eventBus);
		this.eventBus = eventBus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(HashMap<Short, LobbyPlayer> players,
			@Nullable SavedGame savedGame) {
		super.init(players, savedGame);

		// Setup the client systems
		ProcessingSystem<Player> s;
		s = new FirstEventWaveClientSystem(eventBus, localNetworkId);
		s.init(world, sessionSetup.getSeed());
		this.playerSystems.add(s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void fixedUpdate() {
		super.fixedUpdate();

		if (tickCounter.isRightTick(TICKS_PER_SECOND)) {
			clock.update();
		}

		if (tickCounter.isRightTick(15)) {
			Log.debug("CLOCK", "%02d:%02d", clock.getHour(), clock.getMinute());
		}
	}

	/**
	 * @return the clock used to determine the current in-game time.
	 */
	public GameClock getClock() {
		return clock;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void startNextRound() {
		super.startNextRound();

		clock.resetClock();
	}

	@Override
	protected void onNewBallot(@Nullable Ballot ballot) {
		eventBus.post(new NewBallotEvent(ballot));
	}
}
