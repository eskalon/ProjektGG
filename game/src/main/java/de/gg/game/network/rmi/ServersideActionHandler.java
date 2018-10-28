package de.gg.game.network.rmi;

import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;
import com.google.common.base.Preconditions;

import de.gg.engine.log.Log;
import de.gg.engine.network.BaseGameServer;
import de.gg.engine.utils.CollectionUtils;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.entities.Position;
import de.gg.game.network.GameServer;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.session.AuthoritativeSession;
import de.gg.game.types.GameSpeed;
import de.gg.game.types.PositionType;
import de.gg.game.utils.PlayerUtils;
import de.gg.game.votes.ImpeachmentVote;
import de.gg.game.votes.VoteUtils;
import de.gg.game.world.World;

public class ServersideActionHandler implements SlaveActionListener {
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
			Log.error("Server", "Der resultListener des Spielers %d ist null",
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
			VoteResults result = new VoteResults(
					VoteUtils.getVoteResult(session.getMatterToVoteOn(),
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

		Log.info("Server", "Spieler %d ist für nächste Runde bereit",
				networkId);

		if (PlayerUtils.areAllPlayersReady(server.getPlayers().values())) {
			startNextRoundForEveryone();
		}

		return true;
	}

	private synchronized void startNextRoundForEveryone() {
		Log.info("Server", "Alle Spieler sind für die Runde bereit");

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

			world.getMattersToHoldVoteOn().add(new ImpeachmentVote(world, t,
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
		clientResultListeners.onChatMessageSent(clientId, msg);
	}

	@Override
	public void onPlayerChanged(short clientId, LobbyPlayer player) {
		Log.debug("Server",
				"Die Konfiguration von Spieler %d hat sich geändert", clientId);
		server.getPlayers().put(clientId, player);
		clientResultListeners.onLobbyPlayerChanged(clientId, player);

		if (PlayerUtils.areAllPlayersReady(server.getPlayers().values())) {
			server.initGameSession();
		}
	}

}
