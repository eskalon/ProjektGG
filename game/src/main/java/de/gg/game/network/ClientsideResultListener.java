package de.gg.game.network;

import java.util.HashMap;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.log.Log;
import de.gg.game.events.AllPlayersReadyEvent;
import de.gg.game.events.BallotFinishedEvent;
import de.gg.game.events.ChangedGameSpeedEvent;
import de.gg.game.events.LobbyDataReceivedEvent;
import de.gg.game.events.ServerReadyEvent;
import de.gg.game.events.UIRefreshEvent;
import de.gg.game.model.World;
import de.gg.game.model.types.GameSpeed;
import de.gg.game.model.types.PositionType;
import de.gg.game.model.votes.BallotResults;
import de.gg.game.model.votes.ImpeachmentBallot;
import de.gg.game.network.rmi.AuthoritativeResultListener;
import de.gg.game.session.GameSession;
import de.gg.game.session.GameSessionSetup;
import de.gg.game.session.SavedGame;
import de.gg.game.ui.data.ChatMessage;

public class ClientsideResultListener implements AuthoritativeResultListener {

	private GameClient client;
	private EventBus eventBus;
	private GameSession session;
	private World world;

	public ClientsideResultListener(EventBus eventBus, GameClient client) {
		Preconditions.checkNotNull(eventBus);
		Preconditions.checkNotNull(client);

		this.eventBus = eventBus;
		this.client = client;
	}

	public void setSession(GameSession session) {
		Preconditions.checkNotNull(session);
		Preconditions.checkNotNull(session.getWorld());

		this.session = session;
		this.world = session.getWorld();
	}

	@Override
	public void onVoteFinished(BallotResults results) {
		session.finishCurrentVote(results);
		eventBus.post(
				new BallotFinishedEvent(results, session.getMatterToVoteOn()));
	}

	@Override
	public void setGameSpeed(int index) {
		session.setGameSpeed(GameSpeed.values()[index]);

		eventBus.post(new ChangedGameSpeedEvent(session.getGameSpeed()));
	}

	@Override
	public void onImpeachmentVoteArranged(short targetCharacterId,
			short callerCharacterId) {
		world.getMattersToHoldVoteOn()
				.add(new ImpeachmentBallot(world, world.getCharacters()
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

		// TODO Screen instant weiter; Screen sonst _nicht_ von selbst ändern!

		eventBus.post(new ServerReadyEvent());
	}

	@Override
	public void onGameSetup(HashMap<Short, LobbyPlayer> players,
			GameSessionSetup sessionSetup, SavedGame savedGame) {
		Log.info("Client", "Received lobby data");

		client.lobbyPlayers = players;
		client.lobbyData = new LobbyData(sessionSetup, savedGame);

		eventBus.post(new LobbyDataReceivedEvent());
	}

	@Override
	public void onPlayerJoined(short id, LobbyPlayer player) {
		client.lobbyPlayers.put(id, player);
		client.chatMessages.add(new ChatMessage(
				Lang.get("screen.lobby.player_joined", player)));
		eventBus.post(new UIRefreshEvent());
	}

	@Override
	public void onLobbyPlayerChanged(short playerId, LobbyPlayer player) {
		LobbyPlayer local = client.lobbyPlayers.get(playerId);
		local.setName(player.getName());
		local.setSurname(player.getSurname());
		local.setMale(player.isMale());
		local.setIcon(player.getIcon());
		local.setProfessionTypeIndex(player.getProfessionTypeIndex());
		local.setReady(player.isReady());
		local.setReligion(player.getReligion());
		eventBus.post(new UIRefreshEvent());
	}

	@Override
	public void onPlayerLeft(short playerId) {
		client.chatMessages
				.add(new ChatMessage(Lang.get("screen.lobby.player_left",
						client.lobbyPlayers.get(playerId))));
		client.lobbyPlayers.remove(playerId);
		eventBus.post(new UIRefreshEvent());

		if (!client.isInLobby()) {
			// TODO adapt session data
		}
	}

	@Override
	public void onChatMessage(short senderId, String message) {
		if (senderId != client.getLocalNetworkID()) {
			client.chatMessages.add(new ChatMessage(
					client.lobbyPlayers.get(senderId), message));
			eventBus.post(new UIRefreshEvent());
		}
	}

	@Override
	public void onCharacterDeath(short characterId) {
		// TODO Todeseffekte (siehe
		// FirstCharacterEventWaveServerSystem#process())

		// außerdem wenn lokaler Spieler oder Verwandter dann
		// Notification-Banner (-> Banner-Event das UI Reloaded für
		// MapScreen; Notification-List in GameSession)
	}

	// TODO remove:

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
	public void onAppliedForPosition(short characterId, PositionType type) {
		world.getPositions().get(type).getApplicants().add(characterId);
	}

}
