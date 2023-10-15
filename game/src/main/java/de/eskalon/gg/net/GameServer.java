package de.eskalon.gg.net;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.badlogic.gdx.utils.Timer;
import com.esotericsoftware.kryonet.Listener.TypeListener;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.net.LockstepGameServer;
import de.eskalon.commons.net.ServerSettings;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import de.eskalon.gg.misc.PlayerUtils;
import de.eskalon.gg.misc.PlayerUtils.PlayerTemplate;
import de.eskalon.gg.net.packets.CastVotePacket;
import de.eskalon.gg.net.packets.InitVotingPacket;
import de.eskalon.gg.net.packets.VoteFinishedPacket;
import de.eskalon.gg.simulation.GameSetup;
import de.eskalon.gg.simulation.GameState;
import de.eskalon.gg.simulation.MasterSimulation;
import de.eskalon.gg.simulation.SavedGame;
import de.eskalon.gg.simulation.model.votes.Ballot;

public class GameServer
		extends LockstepGameServer<GameSetup, GameState, PlayerData> {

	private static final Logger LOG = LoggerService.getLogger(GameServer.class);

	private List<PlayerTemplate> playerTemplates;

	private MasterSimulation simulation;

	private @Nullable Ballot matterToVoteOn = null;
	private HashMap<Short, Integer> receivedVotes = new HashMap<>();
	private Timer timer = new Timer();

	public GameServer(ServerSettings serverSettings,
			GameSetup sessionSetup, @Nullable SavedGame savedGame,
			List<PlayerTemplate> playerTemplates) {
		super(serverSettings,
				new LobbyData<GameSetup, GameState, PlayerData>(
						sessionSetup, savedGame.state));
		this.playerTemplates = playerTemplates;

		NetworkRegisterer.registerClasses(server.getKryo());

		TypeListener typeListener = new TypeListener();
		typeListener.addTypeHandler(InitVotingPacket.class, (con, msg) -> {
			if (matterToVoteOn != null)
				LOG.error(
						"[SERVER] A second vote was started while the first one was still running");

			matterToVoteOn = msg.getMatterToVoteOn();

			timer.scheduleTask(new Timer.Task() {
				@Override
				public void run() {
					// TODO masterSim#processVoteResults & generate the uncast
					// votes
					server.sendToAllTCP(new VoteFinishedPacket(receivedVotes));
					receivedVotes.clear();
					matterToVoteOn = null;
				}
			}, 10);
		});
		typeListener.addTypeHandler(CastVotePacket.class, (con, msg) -> {
			receivedVotes.put((short) con.getArbitraryData(), msg.getOption());
		});
		server.addListener(typeListener);
	}

	@Override
	public void onAllActionsReceived(List<PlayerActionsWrapper> list) {
		// MasterSimulation#provideActions??
		// MasterSimulation#onSimulationTurn()
	}

	@Override
	protected void onAllPlayersReady() {
		// MasterSim#nextRoundStuff?
	}

	@Override
	protected PlayerData createPlayerData(short id, String hostname) {
		// @formatter:off
//		if (lobbyData.getGameState() != null) { // i.e., loading a previous game
//			short foundId = -1;
//			for (Entry<Short, String> e : saveGame.clientIdentifiers
//					.entrySet()) {
//				if (e.getValue().equals(hostname)) {
//					foundId = e.getKey();
//					break;
//				}
//			}
//
//			if (foundId == -1) {
//				LOG.info(
//						"[SERVER] Kick: Client isn't part of this loaded save game");
//			} else {
//				if (id == HOST_PLAYER_NETWORK_ID
//						&& foundId != HOST_PLAYER_NETWORK_ID) { // i.e., the host has changed
//					LOG.info(
//							"[SERVER] Kick: The host of a loaded save game cannot be changed");
//				}
//				LOG.info(
//						"[SERVER] Client was recognized as part of this loaded save game");
//				Player oldPlayer = savedGame.world.getPlayer(foundId);
//				Character oldCharacter = savedGame.world.getCharacter(
//						oldPlayer.getCurrentlyPlayedCharacterId());
//				return new PlayerData(oldCharacter.getName(),
//						oldCharacter.getSurname(), oldPlayer.getIcon(), -1,
//						oldCharacter.isMale());
//			}
//		}
		// @formatter:on

		return PlayerUtils.getRandomPlayerWithUnusedProperties(playerTemplates,
				lobbyData.getPlayers().values());
	}

}