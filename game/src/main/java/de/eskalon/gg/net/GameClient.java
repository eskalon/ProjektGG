package de.eskalon.gg.net;

import java.util.List;
import java.util.Map.Entry;

import com.esotericsoftware.kryonet.Listener.TypeListener;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.event.EventBus;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.lang.Lang;
import de.eskalon.commons.net.ChatMessage;
import de.eskalon.commons.net.LockstepGameClient;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import de.eskalon.commons.net.packets.sync.LobbyDataChangedPacket.ChangeType;
import de.eskalon.gg.events.AllPlayersReadyEvent;
import de.eskalon.gg.events.VoteFinishedEvent;
import de.eskalon.gg.events.ChatMessageEvent;
import de.eskalon.gg.events.ConnectionLostEvent;
import de.eskalon.gg.events.LobbyDataChangedEvent;
import de.eskalon.gg.net.packets.VoteFinishedPacket;
import de.eskalon.gg.net.packets.CastVotePacket;
import de.eskalon.gg.net.packets.InitVotingPacket;
import de.eskalon.gg.simulation.GameSetup;
import de.eskalon.gg.simulation.GameState;
import de.eskalon.gg.simulation.model.votes.Ballot;

public class GameClient
		extends LockstepGameClient<GameSetup, GameState, PlayerData> {

	private static final Logger LOG = LoggerService.getLogger(GameClient.class);
	private @Inject EventBus eventBus;

	public GameClient() {
		NetworkRegisterer.registerClasses(client.getKryo());

		TypeListener typeListener = new TypeListener();
		typeListener.addTypeHandler(VoteFinishedPacket.class, (con, msg) -> {
			eventBus.post(new VoteFinishedEvent(msg.getIndividualVotes()));
		});
		client.addListener(typeListener);
	}

	@Override
	public void onAllActionsReceived(int turn,
			List<PlayerActionsWrapper> list) {
		// SlaveSim#provideActions??
	}

	@Override
	protected void onNextRound() {
		eventBus.post(new AllPlayersReadyEvent());
	}

	@Override
	protected void onChatMessageReceived(ChatMessage msg) {
		eventBus.post(new ChatMessageEvent<>(msg));
	}

	@Override
	protected void onLobbyDataChanged(
			LobbyData<GameSetup, GameState, PlayerData> lobbyData,
			ChangeType changeType) {
		eventBus.post(new LobbyDataChangedEvent(this.lobbyData, lobbyData,
				changeType));

		if (changeType == ChangeType.PLAYER_JOINED) {
			for (Entry<Short, PlayerData> e : lobbyData.getPlayers()
					.entrySet()) {
				if (!this.lobbyData.getPlayers().containsKey(e.getKey())) {
					eventBus.post(new ChatMessageEvent<>(new ChatMessage<>(Lang
							.get("screen.lobby.player_joined", e.getValue()))));
					break;
				}
			}
		} else if (changeType == ChangeType.PLAYER_LEFT) {
			// TODO chatMessages.add(new ChatMessage<>(""));
		}

	}

	@Override
	protected void onConnectionLost() {
		eventBus.post(new ConnectionLostEvent());
	}

	public PlayerData getLocalLobbyPlayer() {
		return lobbyData.getPlayers().get(localClientId);
	}

	public void arrangeVote() {

	}

	public void castVote(int option) {
		client.sendTCP(new CastVotePacket(option));
	}

	public void initVoting(Ballot matterToVoteOn) {
		client.sendTCP(new InitVotingPacket(matterToVoteOn));
	}

}
