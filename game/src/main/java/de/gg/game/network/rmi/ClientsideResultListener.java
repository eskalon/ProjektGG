package de.gg.game.network.rmi;

import java.util.HashMap;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import de.gg.engine.log.Log;
import de.gg.game.data.vote.VoteResults;
import de.gg.game.events.AllPlayersReadyEvent;
import de.gg.game.events.ChangedGameSpeedEvent;
import de.gg.game.events.GameDataReceivedEvent;
import de.gg.game.events.NewChatMessagEvent;
import de.gg.game.events.PlayerChangedEvent;
import de.gg.game.events.PlayerConnectedEvent;
import de.gg.game.events.PlayerDisconnectedEvent;
import de.gg.game.events.ServerReadyEvent;
import de.gg.game.events.VoteFinishedEvent;
import de.gg.game.network.LobbyPlayer;
import de.gg.game.session.GameSession;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.session.SavedGame;
import de.gg.game.types.GameSpeed;
import de.gg.game.types.PositionType;
import de.gg.game.votes.ImpeachmentVote;
import de.gg.game.world.World;

public class ClientsideResultListener implements AuthoritativeResultListener {

	private final EventBus eventBus;
	private GameSession session;
	private World world;

	public ClientsideResultListener(EventBus eventBus) {
		Preconditions.checkNotNull(eventBus);

		this.eventBus = eventBus;
	}

	public void setSession(GameSession session) {
		Preconditions.checkNotNull(session);
		Preconditions.checkNotNull(session.getWorld());

		this.session = session;
		this.world = session.getWorld();
	}

	@Override
	public void onVoteFinished(VoteResults results) {
		session.finishCurrentVote(results);
		eventBus.post(
				new VoteFinishedEvent(results, session.getMatterToVoteOn()));
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
		world.getCharacters().get(characterId)
				.setHp(world.getCharacters().get(characterId).getHp() - damage);

	}

	@Override
	public void onPlayerIllnessChange(short playerId, boolean isIll) {
		world.getPlayers().get(playerId).setIll(isIll);
	}

	@Override
	public void setGameSpeed(int index) {
		session.setGameSpeed(GameSpeed.values()[index]);

		eventBus.post(new ChangedGameSpeedEvent(session.getGameSpeed()));
	}

	@Override
	public void onAppliedForPosition(short characterId, PositionType type) {
		world.getPositions().get(type).getApplicants().add(characterId);
	}

	@Override
	public void onImpeachmentVoteArranged(short targetCharacterId,
			short callerCharacterId) {
		world.getMattersToHoldVoteOn()
				.add(new ImpeachmentVote(world, world.getCharacters()
						.get(targetCharacterId).getPosition(),
						callerCharacterId));
	}

	@Override
	public synchronized void onAllPlayersReadied() {
		Log.debug("Client", "Alle Spieler sind bereit! Nächste Runde startet");

		session.startNextRound();
		eventBus.post(new AllPlayersReadyEvent(true));
	}

	@Override
	public void onServerReady() {
		Log.info("Client", "Der Server is bereit");
		Log.debug("Client", "%d Ticks hinter der Server-Simulation",
				GameSession.TICKS_PER_ROUND - session.getTickCount());
		eventBus.post(new ServerReadyEvent());
	}

	@Override
	public void onGameSetup(HashMap<Short, LobbyPlayer> players,
			GameSessionSetup sessionSetup, SavedGame savedGame) {
		Log.info("Client", "Game-Setup empfangen");
		eventBus.post(
				new GameDataReceivedEvent(players, sessionSetup, savedGame));
	}

	@Override
	public void onPlayerLeft(short senderId) {
		eventBus.post(new PlayerDisconnectedEvent(senderId));
	}

	@Override
	public void onPlayerJoined(short senderId, LobbyPlayer lobbyPlayer) {
		eventBus.post(new PlayerConnectedEvent(senderId, lobbyPlayer));
	}

	@Override
	public void onLobbyPlayerChanged(short senderId, LobbyPlayer lobbyPlayer) {
		eventBus.post(new PlayerChangedEvent(senderId, lobbyPlayer));
	}

	@Override
	public void onChatMessageSent(short senderId, String message) {
		// TODO chat messages in liste speichern
		eventBus.post(new NewChatMessagEvent(senderId, message));
	}

}
