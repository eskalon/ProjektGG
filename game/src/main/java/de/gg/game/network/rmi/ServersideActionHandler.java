package de.gg.game.network.rmi;

import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.gg.engine.network.BaseGameServer;
import de.gg.engine.utils.CollectionUtils;
import de.gg.game.misc.PlayerUtils;
import de.gg.game.model.World;
import de.gg.game.model.entities.Position;
import de.gg.game.model.types.GameSpeed;
import de.gg.game.model.types.PositionType;
import de.gg.game.model.votes.BallotResults;
import de.gg.game.model.votes.BallotUtils;
import de.gg.game.model.votes.ImpeachmentBallot;
import de.gg.game.network.GameServer;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.session.AuthoritativeSession;

public class ServersideActionHandler implements SlaveActionListener {

	private static final Logger LOG = LoggerService
			.getLogger(ServersideActionHandler.class);

	private final GameServer server;
	private AuthoritativeSession session;
	private World world;
	private ServersideResultListenerStub clientResultListeners;

	public ServersideActionHandler(GameServer server,
			ServersideResultListenerStub clientResultListeners) {
		Preconditions.checkNotNull(server);
		Preconditions.checkNotNull(clientResultListeners);

		this.server = server;
		this.clientResultListeners = clientResultListeners;
	}

	public void setSession(AuthoritativeSession session) {
		Preconditions.checkNotNull(session);
		Preconditions.checkNotNull(session.getWorld());

		this.session = session;
		this.world = session.getWorld();
	}

	@Override
	public void requestGameData(short clientId) {
		// The client established the RMI connection -> now establish it on the
		// server as well
		AuthoritativeResultListener resultListener = ObjectSpace
				.getRemoteObject(
						CollectionUtils.getKeyByValue(server.getConnections(),
								clientId),
						clientId, AuthoritativeResultListener.class);
		if (resultListener == null) {
			LOG.error("[SERVER] Der resultListener des Spielers %d ist null",
					clientId);
			return;
		}
		((RemoteObject) resultListener).setNonBlocking(true);
		server.getResultListeners().put(clientId, resultListener);

		resultListener.onGameSetup(server.getPlayers(),
				server.getSessionSetup(), server.getSavedGame());
	}

	@Override
	public void onVoteCast(int chosenOption, short clientId) {
		session.getIndividualVotes().put(
				world.getPlayer(clientId).getCurrentlyPlayedCharacterId(),
				chosenOption);

		// Check if all votes were made
		if (session.getIndividualVotes().size() == session.getMatterToVoteOn()
				.getVoters().size()) {
			BallotResults result = new BallotResults(
					BallotUtils.getBallotResult(session.getMatterToVoteOn(),
							session.getIndividualVotes(),
							session.getSessionSetup().getSeed()),
					session.getIndividualVotes());
			session.finishCurrentVote(result);
			clientResultListeners.onVoteFinished(result);
		}
	}

	@Override
	public boolean readyUp(short networkId) {
		if (server.getPlayers().get(networkId).isReady()) {
			return false;
		}

		server.getPlayers().get(networkId).setReady(true);

		LOG.info("[SERVER] Spieler %d ist für nächste Runde bereit", networkId);

		if (PlayerUtils.areAllPlayersReady(server.getPlayers().values())) {
			startNextRoundForEveryone();
		}

		return true;
	}

	private synchronized void startNextRoundForEveryone() {
		LOG.info("[SERVER] Alle Spieler sind für die Runde bereit");

		for (LobbyPlayer player : server.getPlayers().values()) {
			player.setReady(false);
		}

		clientResultListeners.onAllPlayersReadied();

		session.startNextRound();
	}

	@Override
	public void increaseGameSpeed(short clientId) {
		if (!server.getServerSetup().isHostOnlyCommands()
				|| clientId == BaseGameServer.HOST_PLAYER_NETWORK_ID) {
			int index = session.getGameSpeed().ordinal() + 1;

			session.setGameSpeed(GameSpeed
					.values()[index >= GameSpeed.values().length ? 0 : index]);
			clientResultListeners
					.setGameSpeed(session.getGameSpeed().ordinal());
		}
	}

	@Override
	public void decreaseGameSpeed(short clientId) {
		if (!server.getServerSetup().isHostOnlyCommands()
				|| clientId == BaseGameServer.HOST_PLAYER_NETWORK_ID) {
			int index = session.getGameSpeed().ordinal() - 1;

			session.setGameSpeed(
					GameSpeed.values()[index < 0 ? GameSpeed.values().length - 1
							: index]);
			clientResultListeners
					.setGameSpeed(session.getGameSpeed().ordinal());
		}
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
	public void onChatmessageSent(short clientId, String msg) {
		clientResultListeners.onChatMessage(clientId, msg);
	}

	@Override
	public void onPlayerChanged(short clientId, LobbyPlayer player) {
		LOG.debug("[SERVER] Die Konfiguration von Spieler %d hat sich geändert",
				clientId);
		server.getPlayers().put(clientId, player);
		clientResultListeners.onLobbyPlayerChanged(clientId, player);

		if (PlayerUtils.areAllPlayersReady(server.getPlayers().values())) {
			server.initGameSession();
		}
	}

}
