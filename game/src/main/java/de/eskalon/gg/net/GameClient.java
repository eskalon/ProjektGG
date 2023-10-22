package de.eskalon.gg.net;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
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
import de.eskalon.gg.events.ChatMessageEvent;
import de.eskalon.gg.events.ConnectionLostEvent;
import de.eskalon.gg.events.LobbyDataChangedEvent;
import de.eskalon.gg.events.VoteFinishedEvent;
import de.eskalon.gg.net.packets.ArrangeVotePacket;
import de.eskalon.gg.net.packets.CastVotePacket;
import de.eskalon.gg.net.packets.InitVotingPacket;
import de.eskalon.gg.net.packets.VoteFinishedPacket;
import de.eskalon.gg.net.packets.data.VoteType;
import de.eskalon.gg.simulation.GameSetup;
import de.eskalon.gg.simulation.GameState;
import de.eskalon.gg.simulation.model.votes.Ballot;

public class GameClient
		extends LockstepGameClient<GameSetup, GameState, PlayerData> {

	private static final Logger LOG = LoggerService.getLogger(GameClient.class);
	private @Inject EventBus eventBus;

	private IntMap<List<PlayerActionsWrapper>> receivedCommands = new IntMap<>(); // TODO
																					// use
																					// better
																					// data
																					// structure
	private Queue<ArrangeVotePacket> mattersToVoteOn = new LinkedList<>();

	public GameClient() {
		NetworkRegisterer.registerClasses(client.getKryo());

		TypeListener typeListener = new TypeListener();
		typeListener.addTypeHandler(ArrangeVotePacket.class, (con, msg) -> {
			mattersToVoteOn.add(msg);
		});
		typeListener.addTypeHandler(VoteFinishedPacket.class, (con, msg) -> {
			eventBus.post(new VoteFinishedEvent(msg.getIndividualVotes()));
		});
		client.addListener(typeListener);
	}

	public List<PlayerActionsWrapper> retrieveActionsForTurn(int turn) {
		return receivedCommands.get(turn);
	}

	@Override
	public void onAllActionsReceived(int turn,
			List<PlayerActionsWrapper> list) {
		receivedCommands.put(turn, list);
	}

	@Override
	protected void onNextRound() {
		eventBus.post(new AllPlayersReadyEvent());

		// Fake the actions for the first two ticks
		receivedCommands.put(0, new ArrayList<>());
		receivedCommands.put(1, new ArrayList<>());
	}

	@Override
	protected void onChatMessageReceived(ChatMessage msg) {
		eventBus.post(new ChatMessageEvent<>(msg));
	}

	@Override
	protected void onLobbyDataChanged(
			LobbyData<GameSetup, GameState, PlayerData> oldData,
			LobbyData<GameSetup, GameState, PlayerData> newData,
			ChangeType changeType) {
		eventBus.post(new LobbyDataChangedEvent(oldData, newData, changeType));

		if (changeType == ChangeType.PLAYER_JOINED) {
			for (Entry<PlayerData> e : lobbyData.getPlayers().entries()) {
				if (!this.lobbyData.getPlayers().containsKey(e.key)) {
					eventBus.post(new ChatMessageEvent<>(new ChatMessage<>(
							Lang.get("screen.lobby.player_joined", e.value))));
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

	public LobbyData<GameSetup, GameState, PlayerData> getLobbyData() {
		return lobbyData;
	}

	public PlayerData getLocalLobbyPlayer() {
		return lobbyData.getPlayers().get(localClientId);
	}

	public void arrangeVote(VoteType impeachment, short callerId,
			short targetId) {
		client.sendTCP(new ArrangeVotePacket(impeachment, callerId, targetId));
	}

	public void castVote(int option) {
		client.sendTCP(new CastVotePacket(option));
	}

	public void initVoting(Ballot matterToVoteOn) {
		client.sendTCP(new InitVotingPacket(matterToVoteOn));
	}

	/**
	 * @return matters on which a vote is held on after this round.
	 */
	public Queue<ArrangeVotePacket> getMattersToHoldVoteOn() {
		return mattersToVoteOn;
	}

}
