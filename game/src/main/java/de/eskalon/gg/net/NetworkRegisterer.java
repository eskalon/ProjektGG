package de.eskalon.gg.net;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryo.Kryo;

import de.eskalon.commons.net.packets.AllPlayersReadyMessage;
import de.eskalon.commons.net.packets.chat.ChatMessageReceivedPacket;
import de.eskalon.commons.net.packets.chat.SendChatMessagePacke;
import de.eskalon.commons.net.packets.data.IPlayerAction;
import de.eskalon.commons.net.packets.data.IReadyable;
import de.eskalon.commons.net.packets.data.LobbyData;
import de.eskalon.commons.net.packets.data.PlayerActionsWrapper;
import de.eskalon.commons.net.packets.handshake.ConnectionEstablishedPacket;
import de.eskalon.commons.net.packets.handshake.ConnectionRejectedPacket;
import de.eskalon.commons.net.packets.handshake.LobbyJoinedPacket;
import de.eskalon.commons.net.packets.handshake.RequestJoiningLobbyPacket;
import de.eskalon.commons.net.packets.lockstep.ActionsDistributionPacket;
import de.eskalon.commons.net.packets.lockstep.SendPlayerActionsPacket;
import de.eskalon.commons.net.packets.sync.ChangeGameSetupPacket;
import de.eskalon.commons.net.packets.sync.ChangePlayerPacket;
import de.eskalon.commons.net.packets.sync.LobbyDataChangedPacket;
import de.eskalon.commons.net.packets.sync.LobbyDataChangedPacket.ChangeType;
import de.eskalon.gg.net.packets.ArrangeVotePacket;
import de.eskalon.gg.net.packets.CastVotePacket;
import de.eskalon.gg.net.packets.VoteFinishedPacket;
import de.eskalon.gg.net.packets.data.VoteType;
import de.eskalon.gg.simulation.GameSetup;
import de.eskalon.gg.simulation.actions.GameSpeedChangeAction;
import de.eskalon.gg.simulation.model.types.GameDifficulty;
import de.eskalon.gg.simulation.model.types.GameMap;
import de.eskalon.gg.simulation.model.types.PlayerIcon;
import de.eskalon.gg.simulation.model.types.PositionType;
import de.eskalon.gg.simulation.model.types.ProfessionType;
import de.eskalon.gg.simulation.model.types.Religion;
import de.eskalon.gg.simulation.model.votes.Ballot;

/**
 * This class takes care of registering all classes needed by the multiplayer
 * endpoints.
 *
 * @see Kryo#register(Class)
 */
public class NetworkRegisterer {

	private NetworkRegisterer() {

	}

	/**
	 * Registers all needed classes to the given Kryo serialization manager.
	 *
	 * @param kryo
	 *            The Kryo serialization manager.
	 */
	public static void registerClasses(Kryo kryo) {
		// Basic classes
		kryo.register(ArrayList.class);
		kryo.register(HashMap.class);
		kryo.register(Object[].class);
		kryo.register(int[].class);
		kryo.register(IntMap.class);

		// Engine packet data
		kryo.register(IPlayerAction.class);
		kryo.register(IReadyable.class);
		kryo.register(LobbyData.class);
		kryo.register(PlayerActionsWrapper.class);

		// Engine packets
		kryo.register(ChatMessageReceivedPacket.class);
		kryo.register(SendChatMessagePacke.class);

		kryo.register(ConnectionEstablishedPacket.class);
		kryo.register(ConnectionRejectedPacket.class);
		kryo.register(RequestJoiningLobbyPacket.class);
		kryo.register(LobbyJoinedPacket.class);

		kryo.register(ActionsDistributionPacket.class);
		kryo.register(SendPlayerActionsPacket.class);

		kryo.register(ChangeGameSetupPacket.class);
		kryo.register(LobbyDataChangedPacket.class);
		kryo.register(ChangeType.class);
		kryo.register(ChangePlayerPacket.class);

		kryo.register(AllPlayersReadyMessage.class);

		// GG packet data
		kryo.register(VoteType.class);

		// GG packets
		kryo.register(ArrangeVotePacket.class);
		kryo.register(VoteFinishedPacket.class);
		kryo.register(CastVotePacket.class);

		// Lobby (Player) Stuff
		kryo.register(PlayerData.class);
		kryo.register(PlayerIcon.class);
		kryo.register(Religion.class);
		kryo.register(ProfessionType.class);

		// Map Stuff
		kryo.register(GameDifficulty.class);
		kryo.register(GameMap.class);
		kryo.register(GameSetup.class);

		// Actions
		kryo.register(GameSpeedChangeAction.class);

		// Elections
		kryo.register(PositionType.class);
		kryo.register(Ballot.class);
	}

}
